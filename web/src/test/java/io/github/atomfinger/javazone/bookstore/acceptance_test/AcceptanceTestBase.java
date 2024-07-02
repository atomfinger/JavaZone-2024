package io.github.atomfinger.javazone.bookstore.acceptance_test;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.MockServerContainer;
import org.mockserver.client.server.MockServerClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTestBase {

  @Container
  public static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("postgres:15-alpine").withDatabaseName(
      "testdb").withUsername("test").withPassword("test");

  public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName
      .parse("mockserver/mockserver")
      .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion());

  @Container
  public MockServerContainer mockServer = new MockServerContainer(MOCKSERVER_IMAGE);

  @Autowired
  public TestRestTemplate restTemplate;

  public static MockServerClient mockServerClient = new MockServerClient(mockserver.getServerPort());

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
  }

  @BeforeEach
  public void resetDb() {
    mockserverClient.reset();
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
