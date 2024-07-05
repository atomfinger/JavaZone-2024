package io.github.atomfinger.javazone.bookstore.kafka.acceptance_test;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.repository.BookRepository;
import io.github.atomfinger.javazone.bookstore.kafka.add_book_listener.AddBookMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
public abstract class AcceptanceTestBase {
    public static PostgreSQLContainer<?> postgresDB;

    public static KafkaContainer kafka;

    static {
        postgresDB = new PostgreSQLContainer<>("postgres:15-alpine").withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgresDB.start();
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
        kafka.start();
    }

    @Autowired
    public BookRepository bookRepository;
    @Autowired
    private Flyway flyway;
    @Autowired
    private KafkaTemplate<String, String> template;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDB::getUsername);
        registry.add("spring.datasource.password", postgresDB::getPassword);
        registry.add("spring.flyway.cleanDisabled", () -> false);
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers());
    }

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();
    }

    public ProducerFactory<String, AddBookMessage> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    public KafkaTemplate<String, AddBookMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
