package io.github.atomfinger.javazone.bookstore.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerExtension;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest(classes = {TestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith({SpringExtension.class, StubRunnerExtension.class})
@AutoConfigureStubRunner(
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        ids = "bookstore:external_orderservice:unspecified:stubs:8080")
class OrderServiceIntegrationTest {

    @Autowired
    public OrderServiceIntegration orderServiceIntegration;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("api.bookstore-endpoint", () -> "http://localhost:8080");
    }

    @Test
    public void letsTest() {
        var result = orderServiceIntegration.listOrdersForBooks(
                List.of(
                        "9780142424179",
                        "9780765326355",
                        "9780061120084"
                )
        );
        Assert.isTrue(false, "");
    }
}