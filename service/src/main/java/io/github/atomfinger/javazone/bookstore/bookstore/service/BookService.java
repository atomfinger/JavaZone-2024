package io.github.atomfinger.javazone.bookstore.bookstore.service;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.entities.Book;
import io.github.atomfinger.javazone.bookstore.bookstore.persistence.repository.BookRepository;
import io.github.atomfinger.javazone.bookstore.integration.web.BestReadsServiceIntegration;
import io.github.atomfinger.javazone.bookstore.integration.web.InventoryServiceIntegration;
import io.github.atomfinger.javazone.bookstore.integration.web.OrderServiceIntegration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;
    private final OrderServiceIntegration orderServiceIntegration;
    private final InventoryServiceIntegration inventoryServiceIntegration;
    private final BestReadsServiceIntegration bestReadsServiceIntegration;

    public BookService(BookRepository repository, OrderServiceIntegration orderServiceIntegration,
                       InventoryServiceIntegration inventoryServiceIntegration,
                       BestReadsServiceIntegration bestReadsServiceIntegration
    ) {
        this.repository = repository;
        this.orderServiceIntegration = orderServiceIntegration;
        this.inventoryServiceIntegration = inventoryServiceIntegration;
        this.bestReadsServiceIntegration = bestReadsServiceIntegration;
    }

    public List<BookListItem> listBooks() {
        var books = ((List<Book>) repository.findAll());
        var isbns = books.stream().map(Book::getIsbn).toList();
        var ordersByIsbn = orderServiceIntegration.listOrdersForBooks(isbns);
        var inventoryByIsbn = inventoryServiceIntegration.findInventoryByISBN(isbns);
        var ratingByIsbn = bestReadsServiceIntegration.getScoresByIsbn(isbns);
        return books.stream().map(book -> new BookListItem(
                book,
                ordersByIsbn.get(book.getIsbn()),
                inventoryByIsbn.get(book.getIsbn()) > 0,
                ratingByIsbn.get(book.getIsbn())
        )).toList();
    }

    public record BookListItem(Book book, Integer orderNumber, boolean isInStock, Integer rating) {
    }
}
