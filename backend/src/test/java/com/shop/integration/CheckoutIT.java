package com.shop.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Le parcours le plus critique de l'appli (panier -> checkout -> stock ->
 * historique) rejoué de bout en bout contre un vrai Postgres — c'est ce
 * test qui aurait attrapé une régression sur la décrémentation de stock ou
 * le vidage du panier après commande.
 */
class CheckoutIT extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    private String registerAndGetToken(String email) throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("email", email, "password", "password123", "firstName", "Checkout", "lastName", "Flow"));
        MvcResult result = mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private long createAddress(String token) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "label", "Domicile", "street", "1 rue Test", "city", "Paris",
                "zipCode", "75000", "country", "France", "defaultAddress", true));
        MvcResult result = mockMvc.perform(post("/api/me/addresses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void fullCheckoutFlow_decrementsStockClearsCartAndAppearsInHistory() throws Exception {
        String token = registerAndGetToken("checkout.flow@example.com");
        long addressId = createAddress(token);

        MvcResult productBefore = mockMvc.perform(get("/api/products/laptop-pro-15"))
                .andExpect(status().isOk())
                .andReturn();
        int stockBefore = objectMapper.readTree(productBefore.getResponse().getContentAsString())
                .get("variants").get(0).get("stock").asInt();

        String cartItemBody = objectMapper.writeValueAsString(Map.of("variantId", 1, "quantity", 2));
        mockMvc.perform(post("/api/me/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartItemBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items[0].quantity", is(2)));

        String checkoutBody = objectMapper.writeValueAsString(Map.of("addressId", addressId));
        mockMvc.perform(post("/api/me/orders/checkout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkoutBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.items[0].quantity", is(2)));

        mockMvc.perform(get("/api/me/cart").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());

        mockMvc.perform(get("/api/products/laptop-pro-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.variants[0].stock", is(stockBefore - 2)));

        mockMvc.perform(get("/api/me/orders").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status", is("CONFIRMED")));
    }

    @Test
    void checkout_withEmptyCart_returnsConflict() throws Exception {
        String token = registerAndGetToken("empty.cart@example.com");
        long addressId = createAddress(token);

        String checkoutBody = objectMapper.writeValueAsString(Map.of("addressId", addressId));
        mockMvc.perform(post("/api/me/orders/checkout")
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
        mockMvc.perform(post("/api/me/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartItemBody))
                .andExpect(status().isConflict());

        String checkoutBody = objectMapper.writeValueAsString(Map.of("addressId", addressId));
        mockMvc.perform(post("/api/me/orders/checkout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkoutBody))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/me/orders").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }
}
