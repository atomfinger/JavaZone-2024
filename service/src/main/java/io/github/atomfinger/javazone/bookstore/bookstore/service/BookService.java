package io.github.atomfinger.javazone.bookstore.bookstore.service;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.entities.Book;
import io.github.atomfinger.javazone.bookstore.bookstore.persistence.repository.BookRepository;
import io.github.atomfinger.javazone.bookstore.integration.kafka.BookCreatedMessageProducer;
import io.github.atomfinger.javazone.bookstore.integration.web.BestReadsServiceIntegration;
import io.github.atomfinger.javazone.bookstore.integration.web.InventoryServiceIntegration;
import io.github.atomfinger.javazone.bookstore.integration.web.OrderServiceIntegration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Fyi: If your service classes looks like this, then you might want to reconsider your chosen architecture.
 */
@Service
public class BookService {

    private final BookRepository repository;
    private final OrderServiceIntegration orderServiceIntegration;
    private final InventoryServiceIntegration inventoryServiceIntegration;
    private final BestReadsServiceIntegration bestReadsServiceIntegration;
    private final BookCreatedMessageProducer bookCreatedMessageProducer;

    public BookService(BookRepository repository, OrderServiceIntegration orderServiceIntegration,
                       InventoryServiceIntegration inventoryServiceIntegration,
                       BestReadsServiceIntegration bestReadsServiceIntegration,
                       BookCreatedMessageProducer bookCreatedMessageProducer
    ) {
        this.repository = repository;
        this.orderServiceIntegration = orderServiceIntegration;
        this.inventoryServiceIntegration = inventoryServiceIntegration;
        this.bestReadsServiceIntegration = bestReadsServiceIntegration;
        this.bookCreatedMessageProducer = bookCreatedMessageProducer;
    }

    @Transactional
    public void addBook(Book book) {
        validateBook(book);
        repository.save(book);
        bookCreatedMessageProducer.notifyBookCreated(book.getBookId(), book.getIsbn());
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

    private static void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }

        if (book.getIsbn() == null || !book.getIsbn().matches("\\d{13}")) {
            throw new IllegalArgumentException("Invalid ISBN. It must be a 13-digit number.");
        }

        if (book.getPageNumbers() <= 0) {
            throw new IllegalArgumentException("Invalid page numbers. It must be a positive integer.");
        }

        if (book.getAuthorName() == null || book.getAuthorName().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid author name. It cannot be empty.");
        }
        String[] nameParts = book.getAuthorName().trim().split(" ");
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Invalid author name. It must include at least a first and last name.");
        }

        if (book.getGenre() == null || book.getGenre().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid genre. It cannot be empty.");
        }

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid title. It cannot be empty.");
        }
    }
}
