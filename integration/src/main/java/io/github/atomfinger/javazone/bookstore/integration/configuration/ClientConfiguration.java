package io.github.atomfinger.javazone.bookstore.integration.configuration;

import io.github.atomfinger.javazone.bookstore.inventoryservice.api.DefaultInventoryServiceClient;
import io.github.atomfinger.javazone.bookstore.orderservice.api.DefaultOrderServiceClient;
import io.github.atomfinger.javazone.bookstore.orderservice.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    private final ApiConfiguration apiConfiguration;

    public ClientConfiguration(ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
    }

    @Bean
    public DefaultOrderServiceClient orderServiceClient() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(apiConfiguration.getOrderEndpoint());
        return new DefaultOrderServiceClient(apiClient);
    }

    @Bean
    public DefaultInventoryServiceClient inventoryServiceClient() {
        var apiClient = new io.github.atomfinger.javazone.bookstore.inventoryservice.invoker.ApiClient();
        apiClient.setBasePath(apiConfiguration.getInventoryEndpoint());
        return new DefaultInventoryServiceClient(apiClient);
    }
}
