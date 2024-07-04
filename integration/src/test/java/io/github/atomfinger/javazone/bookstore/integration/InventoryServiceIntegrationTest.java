package io.github.atomfinger.javazone.bookstore.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.mock.OpenAPIExpectation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test class demonstrates how we can test integrations where we have an OpenAPI spec, but no
 * test environment to test against.
 * <p>
 * This is, however, not limited to OpenAPI. We can test most other schemas in a similar by simply mocking the
 * responses in some way.
 */
class InventoryServiceIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    public void setup() {
        String openApiYaml = null;
        try {
            openApiYaml = new String(Files.readAllBytes(Paths.get("src/main/resources/specs/inventoryservice.yml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mockServerClient.upsert(OpenAPIExpectation.openAPIExpectation(openApiYaml));
    }

    @Test
    public void given_that_we_send_a_valid_request_then_we_should_be_able_parse_the_response() {
        var result = inventoryService.findInventoryByISBN(List.of("9780142424179", "9780765326355", "9780061120084"));
        assertThat(result)
                .containsEntry("9780142424179", 5)
                .containsEntry("9780765326355", 2)
                .containsEntry("9780061120084", 0)
                .hasSize(3);
    }
}