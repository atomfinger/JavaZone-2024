package io.github.atomfinger.javazone.bookstore.bookstore.persistence.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "io.github.atomfinger.javazone.bookstore.bookstore.persistence")
public class PersistenceConfig {
}
