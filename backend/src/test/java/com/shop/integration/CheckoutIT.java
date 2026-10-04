package com.shop.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.payment.PaymentGateway;
import com.shop.payment.PaymentGateway.PaymentSession;
import com.shop.payment.PaymentGateway.PaymentStatus;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Le parcours le plus critique de l'appli (panier -> checkout -> paiement -> stock -> historique)
 * rejoué de bout en bout contre un vrai Postgres — c'est ce test qui aurait attrapé une régression
 * sur la réservation de stock ou le vidage du panier après paiement. Stripe est remplacé par un
 * mock : on vérifie ce que l'appli fait de la réponse de Stripe, pas Stripe lui-même. La clé
 * factice met le checkout en mode STRIPE (le mode DEMO est couvert par DemoCheckoutIT).
 */
@TestPropertySource(
    properties = {"app.stripe.secret-key=sk_test_dummy", "app.stripe.reconcile-enabled=false"})
class CheckoutIT extends AbstractIntegrationTest {

  @Autowired private ObjectMapper objectMapper;

  private static final String TSHIRT_SKU = "OVS-BLANC-M";

  @MockBean private PaymentGateway paymentGateway;

  private String registerAndGetToken(String email) throws Exception {
    String body =
        objectMapper.writeValueAsString(
            Map.of(
                "email",
                email,
                "password",
                "password123",
                "firstName",
                "Checkout",
                "lastName",
                "Flow"));
    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andReturn();
    return objectMapper
        .readTree(result.getResponse().getContentAsString())
        .get("accessToken")
        .asText();
  }

  private long createAddress(String token) throws Exception {
    String body =
        objectMapper.writeValueAsString(
            Map.of(
                "label",
                "Domicile",
                "street",
                "1 rue Test",
                "city",
                "Paris",
                "zipCode",
                "75000",
                "country",
                "France",
                "defaultAddress",
                true));
    MvcResult result =
        mockMvc
            .perform(
                post("/api/me/addresses")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
  }

  /** Variante achetée dans ces tests : RINGER BASIC CONTRAST RIBBED T-SHIRT, White, taille M. */
  private JsonNode tshirtVariant() throws Exception {
    MvcResult product =
        mockMvc.perform(get("/api/products/tshirts")).andExpect(status().isOk()).andReturn();
    for (JsonNode variant :
        objectMapper.readTree(product.getResponse().getContentAsString()).get("variants")) {
      if (TSHIRT_SKU.equals(variant.get("sku").asText())) {
        return variant;
      }
    }
    throw new AssertionError("Seeded variant " + TSHIRT_SKU + " not found");
  }

  private int tshirtStock() throws Exception {
    return tshirtVariant().get("stock").asInt();
  }

  private void addTshirtsToCart(String token, int quantity) throws Exception {
    long variantId = tshirtVariant().get("id").asLong();
    String cartItemBody =
        objectMapper.writeValueAsString(Map.of("variantId", variantId, "quantity", quantity));
    mockMvc
        .perform(
            post("/api/me/cart/items")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cartItemBody))
        .andExpect(status().isCreated());
  }

  /** Lance le checkout et renvoie l'id de la commande PENDING créée. */
  private long checkout(String token, long addressId, String sessionId) throws Exception {
    when(paymentGateway.createSession(any(), any()))
        .thenReturn(
            new PaymentSession(sessionId, "https://checkout.stripe.com/c/pay/" + sessionId));
    String checkoutBody = objectMapper.writeValueAsString(Map.of("addressId", addressId));
    MvcResult result =
        mockMvc
            .perform(
                post("/api/me/orders/checkout")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(checkoutBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.order.status", is("PENDING")))
            .andExpect(jsonPath("$.order.items[0].quantity", is(2)))
            .andExpect(
                jsonPath("$.paymentUrl", is("https://checkout.stripe.com/c/pay/" + sessionId)))
            .andReturn();
    return objectMapper
        .readTree(result.getResponse().getContentAsString())
        .get("order")
        .get("id")
        .asLong();
  }

  @Test
  void fullCheckoutFlow_paidByCard_confirmsOrderClearsCartAndAppearsInHistory() throws Exception {
    String token = registerAndGetToken("checkout.flow@example.com");
    long addressId = createAddress(token);
    int stockBefore = tshirtStock();
    addTshirtsToCart(token, 2);

    long orderId = checkout(token, addressId, "cs_test_paid");

    // Tant que le paiement n'est pas confirmé : stock réservé, panier encore là.
    assertThat(tshirtStock()).isEqualTo(stockBefore - 2);
    mockMvc
        .perform(get("/api/me/cart").header("Authorization", "Bearer " + token))
        .andExpect(jsonPath("$.items[0].quantity", is(2)));

    // Retour de la page Stripe : le paiement est revérifié côté serveur.
    when(paymentGateway.getStatus("cs_test_paid")).thenReturn(PaymentStatus.PAID);
    mockMvc
        .perform(
            post("/api/me/orders/" + orderId + "/payment/confirm")
                .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("CONFIRMED")));

    mockMvc
        .perform(get("/api/me/cart").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isEmpty());

    assertThat(tshirtStock()).isEqualTo(stockBefore - 2);

    mockMvc
        .perform(get("/api/me/orders").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].status", is("CONFIRMED")));
  }

  @Test
  void cancelledPayment_cancelsOrderRestoresStockAndKeepsCart() throws Exception {
    String token = registerAndGetToken("payment.cancelled@example.com");
    long addressId = createAddress(token);
    int stockBefore = tshirtStock();
    addTshirtsToCart(token, 2);

    long orderId = checkout(token, addressId, "cs_test_cancelled");

    when(paymentGateway.getStatus("cs_test_cancelled")).thenReturn(PaymentStatus.EXPIRED);
    mockMvc
        .perform(
            post("/api/me/orders/" + orderId + "/payment/cancel")
                .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("CANCELLED")));

    assertThat(tshirtStock()).isEqualTo(stockBefore);
    mockMvc
        .perform(get("/api/me/cart").header("Authorization", "Bearer " + token))
        .andExpect(jsonPath("$.items[0].quantity", is(2)));
  }

  @Test
  void checkout_withEmptyCart_returnsConflict() throws Exception {
    String token = registerAndGetToken("empty.cart@example.com");
    long addressId = createAddress(token);

    String checkoutBody = objectMapper.writeValueAsString(Map.of("addressId", addressId));
    mockMvc
        .perform(
            post("/api/me/orders/checkout")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutBody))
        .andExpect(status().isConflict());
  }

  @Test
  void checkout_withInsufficientStock_returnsConflictAndDoesNotCreateOrder() throws Exception {
    String token = registerAndGetToken("insufficient.stock@example.com");
    long addressId = createAddress(token);

    // variant id=5 (TS-L-BLANC) est seedé avec un stock de 0
    String cartItemBody = objectMapper.writeValueAsString(Map.of("variantId", 5, "quantity", 1));
    mockMvc
        .perform(
            post("/api/me/cart/items")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cartItemBody))
        .andExpect(status().isConflict());

    String checkoutBody = objectMapper.writeValueAsString(Map.of("addressId", addressId));
    mockMvc
        .perform(
            post("/api/me/orders/checkout")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutBody))
        .andExpect(status().isConflict());

    mockMvc
        .perform(get("/api/me/orders").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty());
  }
}
