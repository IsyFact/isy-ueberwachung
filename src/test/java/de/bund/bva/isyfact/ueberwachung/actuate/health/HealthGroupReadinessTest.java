package de.bund.bva.isyfact.ueberwachung.actuate.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyHealthAutoConfiguration;

/**
 * Verifies that the readiness health group configured in health.properties
 * is properly exposed and includes the expected health contributors.
 *
 * <p>Property under test:
 * <pre>
 * management.endpoint.health.group.readiness.include=readinessState,isyNachbarsystem
 * </pre>
 */
@AutoConfigureWebTestClient
@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = { IsyHealthAutoConfiguration.class, HealthGroupReadinessTest.TestConfig.class },
    properties = {
        "isy.logging.anwendung.name=HealthGroupReadinessTest",
        "isy.logging.anwendung.version=1.0.0-SNAPSHOT",
        "isy.logging.anwendung.typ=Integrationstest",
        "management.endpoint.health.group.readiness.include=readinessState,isyNachbarsystem",
        "management.endpoint.health.group.readiness.show-details=always"
    }
)
public class HealthGroupReadinessTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    WebEndpointProperties webEndpointProperties;

    @Test
    void readinessEndpoint_isAccessibleWithoutAuth() {
        healthCall("/health/readiness")
            .expectStatus().isOk();
    }

    @Test
    void readinessEndpoint_returnsStatusUp() {
        Map<String, Object> body = healthCall("/health/readiness")
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
            })
            .returnResult().getResponseBody();

        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("UP");
    }

    @Test
    void readinessEndpoint_includesReadinessStateComponent() {
        Map<String, Object> body = healthCall("/health/readiness")
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
            })
            .returnResult().getResponseBody();

        assertThat(body).isNotNull();
        @SuppressWarnings("unchecked")
        Map<String, Object> components = (Map<String, Object>) body.get("components");
        assertThat(components)
            .as("readiness group must include 'readinessState'")
            .containsKey("readinessState");
    }

    @Test
    void readinessEndpoint_includesNachbarsystemComponent() {
        Map<String, Object> body = healthCall("/health/readiness")
            .expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
            })
            .returnResult().getResponseBody();

        assertThat(body).isNotNull();
        @SuppressWarnings("unchecked")
        Map<String, Object> components = (Map<String, Object>) body.get("components");
        assertThat(components)
            .as("readiness group must include 'isyNachbarsystem'")
            .containsKey("isyNachbarsystem");
    }

    private WebTestClient.ResponseSpec healthCall(String path) {
        return webClient.get()
            .uri(webEndpointProperties.getBasePath() + path)
            .exchange();
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }
}
