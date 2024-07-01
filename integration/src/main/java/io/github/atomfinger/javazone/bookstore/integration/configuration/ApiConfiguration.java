package io.github.atomfinger.javazone.bookstore.integration.configuration;

import io.github.atomfinger.javazone.bookstore.orderservice.api.DefaultOrderServiceClient;
import io.github.atomfinger.javazone.bookstore.orderservice.invoker.ApiClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api")
public class ApiConfiguration {

    private String bookstoreEndpoint;

    public String getBookstoreEndpoint() {
        return bookstoreEndpoint;
    }

    public void setBookstoreEndpoint(String bookstoreEndpoint) {
        this.bookstoreEndpoint = bookstoreEndpoint;
    }
}
