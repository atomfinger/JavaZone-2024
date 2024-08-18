package io.github.atomfinger.javazone.bookstore.integration.contract_tests;

import io.github.atomfinger.javazone.bookstore.integration.web.OrderServiceIntegration;
import io.github.atomfinger.javazone.bookstore.integration.TestApplication;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = { TestApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith({ SpringExtension.class, StubRunnerExtension.class })
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL, ids = "bookstore:external_orderservice:unspecified:stubs:8080")
class OrderServiceIntegrationTest {

    @Autowired
    public OrderServiceIntegration orderServiceIntegration;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("api.order-endpoint", () -> "http://localhost:8080");
        registry.add("spring.kafka.bootstrap-servers", () -> "http://localhost:1234");
    }

    @Test
    public void given_a_valid_list_of_isbns_then_we_should_get_a_list_back_with_number_of_orders() {
        var result = orderServiceIntegration.listOrdersForBooks(
                List.of(
                        "9780142424179",
                        "9780765326355",
                        "9780061120084"));
        assertThat(result)
                .containsEntry("9780142424179", 100)
                .containsEntry("9780765326355", 42)
                .containsEntry("9780061120084", 0)
                .hasSize(3);
    }
}
