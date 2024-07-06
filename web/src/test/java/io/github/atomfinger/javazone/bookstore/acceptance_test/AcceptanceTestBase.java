package io.github.atomfinger.javazone.bookstore.acceptance_test;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTestBase {

    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName.parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion());
    public static PostgreSQLContainer<?> postgresDB;
    public static MockServerContainer mockServerContainer;

    static {
        mockServerContainer = new MockServerContainer(MOCKSERVER_IMAGE);
        mockServerContainer.start();
        postgresDB = new PostgreSQLContainer<>("postgres:15-alpine").withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgresDB.start();
    }

    @Autowired
    public TestRestTemplate restTemplate;
    public MockServerClient mockServerClient = new MockServerClient("localhost", mockServerContainer.getServerPort());
    @LocalServerPort
    private int port;
    @Autowired
    private Flyway flyway;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDB::getUsername);
        registry.add("spring.datasource.password", postgresDB::getPassword);
        registry.add("spring.flyway.cleanDisabled", () -> false);
        registry.add("api.order-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort() + "/order");
        registry.add("api.inventory-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort() + "/inventory");
        registry.add("api.best-reads-endpoint", () -> "http://localhost:" + mockServerContainer.getServerPort() + "/best-reads");
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:1234");
    }

    @BeforeEach
    public void reset() {
        mockServerClient.reset();
        flyway.clean();
        flyway.migrate();
    }

    /**
     * Creates the URL for the service running locally.
     */
    public String getUrl() {
        return "http://localhost:" + port + "/api";
    }
}
