package io.github.atomfinger.javazone.bookstore.kafka.acceptance_test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
class AddBookListenerTest {

    public static PostgreSQLContainer<?> postgresDB;

    public static KafkaContainer kafka;

    static {
        postgresDB = new PostgreSQLContainer<>("postgres:15-alpine").withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgresDB.start();
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));
        kafka.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDB::getUsername);
        registry.add("spring.datasource.password", postgresDB::getPassword);
        registry.add("spring.flyway.cleanDisabled", () -> false);
        //TODO: Add kafka specific configs
    }

}