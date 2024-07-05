package io.github.atomfinger.javazone.bookstore.integration.web;

import io.github.atomfinger.javazone.bookstore.orderservice.api.DefaultOrderServiceClient;
import io.github.atomfinger.javazone.bookstore.orderservice.model.BookOrder;
import io.github.atomfinger.javazone.bookstore.orderservice.model.ISBN;
import io.github.atomfinger.javazone.bookstore.orderservice.model.OrdersPostRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceIntegration {

    private final DefaultOrderServiceClient orderServiceClient;

    public OrderServiceIntegration(DefaultOrderServiceClient orderServiceClient) {
        this.orderServiceClient = orderServiceClient;
    }

    public Map<String, Integer> listOrdersForBooks(List<String> isbns) {
        var request = new OrdersPostRequest();
        request.isbns(isbns.stream().map(x -> {
            var isbn = new ISBN();
            isbn.code(x);
            return isbn;
        }).toList());
        var response = orderServiceClient.ordersPost(request).getData();
        return Objects.requireNonNull(response)
                .stream()
                .collect(Collectors.toMap(bookOrder -> bookOrder.getIsbn().getCode(), BookOrder::getOrderCount));
    }
}
