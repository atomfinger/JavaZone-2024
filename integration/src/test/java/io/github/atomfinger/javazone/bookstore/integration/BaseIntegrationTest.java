package io.github.atomfinger.javazone.bookstore.integration;

import io.github.atomfinger.javazone.bookstore.integration.web.InventoryServiceIntegration;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = {TestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseIntegrationTest {

    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName.parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion());

    public static KafkaContainer kafka;
    public static MockServerContainer mockServerContainer;

    static {
        mockServerContainer = new MockServerContainer(MOCKSERVER_IMAGE);
        mockServerContainer.start();
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
        kafka.start();
    }

    public MockServerClient mockServerClient = new MockServerClient("localhost", mockServerContainer.getServerPort());

    @Autowired
    public InventoryServiceIntegration inventoryService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("api.inventory-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort());
        registry.add("api.best-reads-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort());
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers());
    }

    @BeforeEach
    public void setup() {
        mockServerClient.reset();
    }
}
