package io.github.atomfinger.javazone.bookstore.integration;

import io.github.atomfinger.javazone.bookstore.integration.web.InventoryServiceIntegration;
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
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Testcontainers
@SpringBootTest(classes = {TestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseIntegrationTest {

    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName.parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion());

    private static final String KAFKA_TOPIC_NAME = "bookstore.fct.book-added.1";

    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2"))
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));

    @Container
    public static MockServerContainer mockServerContainer = new MockServerContainer(MOCKSERVER_IMAGE)
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));

    public MockServerClient mockServerClient;

    @Autowired
    public InventoryServiceIntegration inventoryService;

    @BeforeAll
    static void setupKafkaTopic() {
        System.out.println("Attempting to set up Kafka topic...");
        if (!kafka.isRunning()) {
            System.out.println("Kafka container is not running before attempting topic creation!");
            // This is unexpected if @Container is working correctly.
            // Testcontainers should ensure the container is started before @BeforeAll.
            // We won't manually start it here as it might interfere with Testcontainers lifecycle.
            // If this log appears, it indicates a more fundamental issue with Testcontainers setup or environment.
        }

        String bootstrapServers = null;
        try {
            bootstrapServers = kafka.getBootstrapServers();
            System.out.println("Kafka bootstrap servers: " + bootstrapServers);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to get Kafka bootstrap servers: " + e.getMessage());
            e.printStackTrace();
            // If we can't get bootstrap servers, AdminClient creation will likely fail.
            // Re-throw or handle appropriately if this indicates a critical setup failure.
            throw new RuntimeException("Failed to get Kafka bootstrap servers, cannot proceed with topic creation.", e);
        }

        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Increase request timeout for AdminClient, just in case of slow environment
        config.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000"); // 30 seconds
        config.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "30000"); // 30 seconds


        System.out.println("Attempting to create AdminClient for Kafka...");
        try (AdminClient admin = AdminClient.create(config)) {
            System.out.println("AdminClient created successfully.");
            NewTopic topic = new NewTopic(KAFKA_TOPIC_NAME, 1, (short) 1);
            System.out.println("Attempting to create Kafka topic: " + KAFKA_TOPIC_NAME);
            admin.createTopics(Collections.singleton(topic)).all().get(); // Using .get() to wait for completion
            System.out.println("Kafka topic '" + KAFKA_TOPIC_NAME + "' creation request submitted and completed.");

            // Optional: Add a small delay to see if it helps, though .get() should ensure completion.
            // Thread.sleep(1000);
            // System.out.println("Added small delay after topic creation.");

        } catch (Exception e) {
            System.err.println("ERROR: Failed to create Kafka topic '" + KAFKA_TOPIC_NAME + "': " + e.getMessage());
            e.printStackTrace();
            // Depending on test strategy, you might want to throw an exception here
            // to fail fast if topic creation is essential for all tests.
            throw new RuntimeException("Failed to create Kafka topic " + KAFKA_TOPIC_NAME, e);
        }
        System.out.println("Kafka topic setup finished.");
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("api.inventory-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort());
        registry.add("api.best-reads-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort());
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.producer.retries", () -> "3"); // Add producer retries
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest"); // Good for tests
        registry.add("spring.kafka.consumer.group-id", () -> "test-consumer-group"); // Define a group id
    }

    @BeforeEach
    public void setup() {
        mockServerClient = new MockServerClient(mockServerContainer.getHost(), mockServerContainer.getServerPort());
        mockServerClient.reset();
    }
}
