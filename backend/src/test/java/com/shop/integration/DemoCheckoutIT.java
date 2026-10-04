package com.shop.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Checkout en mode DEMO (aucune clé Stripe) : la carte est saisie dans le formulaire intégré et
 * contrôlée par DemoCardProcessor. Une carte acceptée confirme la commande tout de suite ; une
 * carte refusée ne crée rien et ne touche pas au stock.
 */
class DemoCheckoutIT extends AbstractIntegrationTest {

  private static final String TSHIRT_SKU = "OVS-BLANC-M";

  @Autowired private ObjectMapper objectMapper;

  private String registerAndGetToken(String email) throws Exception {
    String body =
        objectMapper.writeValueAsString(
            Map.of(
                "email", email, "password", "password123", "firstName", "Demo", "lastName", "Pay"));
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
                "street", "1 rue Test", "city", "Paris", "zipCode", "75000", "country", "France"));
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

  private void addTshirtToCart(String token) throws Exception {
    String body =
        objectMapper.writeValueAsString(
            Map.of("variantId", tshirtVariant().get("id").asLong(), "quantity", 1));
    mockMvc
        .perform(
            post("/api/me/cart/items")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated());
  }

  private String checkoutBody(long addressId, String cardNumber) throws Exception {
    return objectMapper.writeValueAsString(
        Map.of(
            "addressId",
            addressId,
            "card",
            Map.of(
                "number",
                cardNumber,
                "expMonth",
                12,
                "expYear",
                2035,
                "cvc",
                "123",
                "holderName",
                "Jane Doe")));
  }

  @Test
  void paymentConfig_reportsDemoModeWithoutStripeKey() throws Exception {
    String token = registerAndGetToken("demo.config@example.com");
    mockMvc
        .perform(get("/api/me/payment/config").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.provider", is("DEMO")));
  }

  @Test
  void approvedCard_confirmsOrderImmediatelyAndKeepsOnlyLast4() throws Exception {
    String token = registerAndGetToken("demo.approved@example.com");
    long addressId = createAddress(token);
    int stockBefore = tshirtVariant().get("stock").asInt();
    addTshirtToCart(token);

    mockMvc
        .perform(
            post("/api/me/orders/checkout")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutBody(addressId, "4242 4242 4242 4242")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.order.status", is("CONFIRMED")))
        .andExpect(jsonPath("$.order.cardBrand", is("Visa")))
        .andExpect(jsonPath("$.order.cardLast4", is("4242")))
        .andExpect(jsonPath("$.paymentUrl", nullValue()));

    mockMvc
        .perform(get("/api/me/cart").header("Authorization", "Bearer " + token))
        .andExpect(jsonPath("$.items").isEmpty());
    assertThat(tshirtVariant().get("stock").asInt()).isEqualTo(stockBefore - 1);
  }

  @Test
  void declinedCard_returns402AndCreatesNothing() throws Exception {
    String token = registerAndGetToken("demo.declined@example.com");
    long addressId = createAddress(token);
    int stockBefore = tshirtVariant().get("stock").asInt();
    addTshirtToCart(token);

    mockMvc
        .perform(
            post("/api/me/orders/checkout")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(checkoutBody(addressId, "4000 0000 0000 0002")))
        .andExpect(status().isPaymentRequired())
        .andExpect(jsonPath("$.message", is("Your card was declined.")));

    mockMvc
        .perform(get("/api/me/orders").header("Authorization", "Bearer " + token))
        .andExpect(jsonPath("$.content").isEmpty());
    mockMvc
        .perform(get("/api/me/cart").header("Authorization", "Bearer " + token))
        .andExpect(jsonPath("$.items[0].quantity", is(1)));
    assertThat(tshirtVariant().get("stock").asInt()).isEqualTo(stockBefore);
  }
}
