package io.github.atomfinger.javazone.bookstore.integration;

import io.github.atomfinger.javazone.bookstore.integration.web.InventoryServiceIntegration;
import io.github.atomfinger.javazone.bookstore.integration.web.InventoryServiceIntegration;
import io.github.atomfinger.javazone.bookstore.integration.web.InventoryServiceIntegration;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Testcontainers
@SpringBootTest(classes = {TestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseIntegrationTest {

    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName.parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion());

    private static final String KAFKA_TOPIC_NAME = "bookstore.fct.book-added.1";

    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2"));

    @Container
    public static MockServerContainer mockServerContainer = new MockServerContainer(MOCKSERVER_IMAGE);

    public MockServerClient mockServerClient;

    @Autowired
    public InventoryServiceIntegration inventoryService;

    @BeforeAll
    static void setupKafkaTopic() throws ExecutionException, InterruptedException {
        // Ensure Kafka container is running before trying to create a topic
        if (!kafka.isRunning()) {
            // This should ideally not happen if @Container behaves as expected,
            // but as a safeguard or if running outside full JUnit5 lifecycle.
            // However, direct start() here might conflict with Testcontainers own lifecycle.
            // For now, we rely on Testcontainers to start it before @BeforeAll.
        }

        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        try (AdminClient admin = AdminClient.create(config)) {
            NewTopic topic = new NewTopic(KAFKA_TOPIC_NAME, 1, (short) 1);
            admin.createTopics(Collections.singleton(topic)).all().get();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("api.inventory-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort());
        registry.add("api.best-reads-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort());
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest"); // Good for tests
        registry.add("spring.kafka.consumer.group-id", () -> "test-consumer-group"); // Define a group id
    }

    @BeforeEach
    public void setup() {
        mockServerClient = new MockServerClient(mockServerContainer.getHost(), mockServerContainer.getServerPort());
        mockServerClient.reset();
    }
}
