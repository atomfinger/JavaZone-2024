package io.github.atomfinger.javazone.bookstore.integration;

import io.github.atomfinger.javazone.bookstore.inventoryservice.api.DefaultInventoryServiceClient;
import io.github.atomfinger.javazone.bookstore.inventoryservice.model.GetInventory200ResponseInventoryInner;
import io.github.atomfinger.javazone.bookstore.inventoryservice.model.GetInventoryRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryServiceIntegration {

    private final DefaultInventoryServiceClient inventoryServiceClient;

    public InventoryServiceIntegration(DefaultInventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public Map<String, Integer> findInventoryByISBN(List<String> isbns) {
        var request = new GetInventoryRequest();
        request.isbns(isbns);
        var response = inventoryServiceClient.getInventory(request);
        return response.getInventory()
                .stream()
                .collect(Collectors.toMap(
                        GetInventory200ResponseInventoryInner::getIsbn,
                        GetInventory200ResponseInventoryInner::getCount
                ));
    }
}
