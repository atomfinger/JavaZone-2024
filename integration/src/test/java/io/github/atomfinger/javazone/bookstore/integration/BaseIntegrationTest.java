package io.github.atomfinger.javazone.bookstore.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {TestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseIntegrationTest {

    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName.parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion());

    public static MockServerContainer mockServerContainer;

    static {
        mockServerContainer = new MockServerContainer(MOCKSERVER_IMAGE);
        mockServerContainer.start();
    }

    public MockServerClient mockServerClient = new MockServerClient("localhost", mockServerContainer.getServerPort());

    @Autowired
    public InventoryServiceIntegration inventoryService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("api.inventory-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort());
        registry.add("api.best-reads-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort());
    }

    @BeforeEach
    public void setup() {
        mockServerClient.reset();
    }
}
