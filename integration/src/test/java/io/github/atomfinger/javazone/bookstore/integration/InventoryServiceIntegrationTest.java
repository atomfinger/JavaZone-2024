package io.github.atomfinger.javazone.bookstore.integration;

import io.github.atomfinger.javazone.bookstore.integration.configuration.ApiConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.mock.OpenAPIExpectation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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
@Testcontainers
@SpringBootTest(classes = {TestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class InventoryServiceIntegrationTest {

    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName.parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion());

    @Container
    public static MockServerContainer mockServerContainer = new MockServerContainer(MOCKSERVER_IMAGE);

    public MockServerClient mockServerClient = new MockServerClient("localhost", mockServerContainer.getServerPort());

    @Autowired
    public InventoryServiceIntegration inventoryService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("api.inventory-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort());
    }

    @BeforeEach
    public void setup() throws IOException {
        String openApiYaml = new String(Files.readAllBytes(Paths.get("src/main/resources/specs/inventoryservice.yml")));
        mockServerClient.upsert(OpenAPIExpectation.openAPIExpectation(openApiYaml));
    }

    @Test
    public void given_that_we_send_a_valid_request_then_we_should_be_able_parse_the_response() {
        var result = inventoryService.findInventoryByISBN(List.of("9780142424179", "9780765326355", "9780061120084"));
        assertThat(result)
                .containsEntry("9780142424179", 5)
                .containsEntry( "9780765326355", 2)
                .containsEntry( "9780061120084", 0)
                .hasSize(3);
    }
}