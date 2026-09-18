package com.shop.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Base commune des tests d'intégration : démarre un vrai Postgres jetable
 * via Testcontainers (pas de H2 — on veut tester contre le même moteur de
 * base qu'en production, JSONB de {@code ProductVariant.attributes} inclus,
 * qui n'a pas d'équivalent fidèle en H2).
 *
 * {@code @ServiceConnection} configure automatiquement le datasource Spring
 * à partir du conteneur — pas de propriétés à câbler à la main.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    @Autowired
    protected MockMvc mockMvc;
}
