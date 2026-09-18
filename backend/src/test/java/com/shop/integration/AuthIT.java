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

class AuthIT extends AbstractIntegrationTest {

  @Autowired private ObjectMapper objectMapper;

  @Test
  void registerThenLogin_returnsAccessToken() throws Exception {
    String email = "integration.test@example.com";
    String registerBody =
        objectMapper.writeValueAsString(
            Map.of(
                "email",
                email,
                "password",
                "password123",
                "firstName",
                "Integration",
                "lastName",
                "Test"));

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.user.email", is(email)))
        .andExpect(jsonPath("$.user.roles[0]", is("ROLE_USER")));

    String loginBody =
        objectMapper.writeValueAsString(Map.of("email", email, "password", "password123"));

    mockMvc
        .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists());
  }

  @Test
  void register_withDuplicateEmail_returnsConflict() throws Exception {
    String email = "duplicate@example.com";
    String body =
        objectMapper.writeValueAsString(
            Map.of("email", email, "password", "password123", "firstName", "A", "lastName", "B"));

    mockMvc
        .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated());

    mockMvc
        .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status", is(409)));
  }

  @Test
  void register_withInvalidPayload_returnsBadRequestWithFieldErrors() throws Exception {
    String body =
        objectMapper.writeValueAsString(
            Map.of(
                "email", "not-an-email", "password", "short", "firstName", "", "lastName", "Test"));

    mockMvc
        .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fieldErrors").isArray());
  }

  @Test
  void publicCatalog_categoriesAccessibleWithoutAuth() throws Exception {
    mockMvc.perform(get("/api/categories")).andExpect(status().isOk());
  }

  @Test
  void adminEndpoint_withoutAuth_isForbidden() throws Exception {
    mockMvc.perform(get("/api/admin/orders")).andExpect(status().isForbidden());
  }
}
