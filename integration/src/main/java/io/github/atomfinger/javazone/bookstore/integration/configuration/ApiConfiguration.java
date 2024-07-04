package io.github.atomfinger.javazone.bookstore.integration.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api")
public class ApiConfiguration {

    private String orderEndpoint;
    private String inventoryEndpoint;

    public String getOrderEndpoint() {
        return orderEndpoint;
    }

    public void setOrderEndpoint(String orderEndpoint) {
        this.orderEndpoint = orderEndpoint;
    }

    public String getInventoryEndpoint() {
        return inventoryEndpoint;
    }

    public void setInventoryEndpoint(String inventoryEndpoint) {
        this.inventoryEndpoint = inventoryEndpoint;
    }
}
