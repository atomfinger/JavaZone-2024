package io.github.atomfinger.javazone.bookstore.integration.configuration;

import io.github.atomfinger.javazone.bookstore.orderservice.api.DefaultOrderServiceClient;
import io.github.atomfinger.javazone.bookstore.orderservice.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ApiConfiguration {

    private String bookstoreEndpoint;

    public String getBookstoreEndpoint() {
        return bookstoreEndpoint;
    }

    public void setBookstoreEndpoint(String bookstoreEndpoint) {
        this.bookstoreEndpoint = bookstoreEndpoint;
    }

    @Bean
    public DefaultOrderServiceClient orderServiceClient(String bookstoreEndpoint) {
        var apiClient = new ApiClient();
        apiClient.setBasePath(bookstoreEndpoint);
        return new DefaultOrderServiceClient(apiClient);
    }

}
