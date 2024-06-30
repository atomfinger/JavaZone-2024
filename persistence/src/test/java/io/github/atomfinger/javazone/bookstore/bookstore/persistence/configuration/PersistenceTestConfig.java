package io.github.atomfinger.javazone.bookstore.bookstore.persistence.configuration;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.BaseDatabaseIntegrationTest;
import org.flywaydb.core.Flyway;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@TestConfiguration
public class PersistenceTestConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        var postgresContainer = BaseDatabaseIntegrationTest.postgresDB;
        return DataSourceBuilder.create()
                .url(postgresContainer.getJdbcUrl())
                .username(postgresContainer.getUsername())
                .password(postgresContainer.getPassword())
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Bean
    @Primary
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migrations")
                .cleanDisabled(false)
                .load();
    }
}
