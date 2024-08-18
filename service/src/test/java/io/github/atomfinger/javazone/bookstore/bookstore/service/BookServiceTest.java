package io.github.atomfinger.javazone.bookstore.service;

import io.github.atomfinger.javazone.bookstore.persistence.entities.Book;
import io.github.atomfinger.javazone.bookstore.persistence.repository.BookRepository;
import io.github.atomfinger.javazone.bookstore.integration.kafka.BookCreatedMessageProducer;
import io.github.atomfinger.javazone.bookstore.integration.web.BestReadsServiceIntegration;
import io.github.atomfinger.javazone.bookstore.integration.web.InventoryServiceIntegration;
import io.github.atomfinger.javazone.bookstore.integration.web.OrderServiceIntegration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * These tests represent the problems of not separating application code and business logic.
 * <p>
 * Notice how much effort that goes into mocking - and how much that couples the test to the implementation of a given
 * feature.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    @Mock
    private OrderServiceIntegration orderServiceIntegration;

    @Mock
    private InventoryServiceIntegration inventoryServiceIntegration;

    @Mock
    private BestReadsServiceIntegration bestReadsServiceIntegration;

    @Mock
    private BookCreatedMessageProducer bookCreatedMessageProducer;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setIsbn("9780134685991");
        book.setPageNumbers(320);
        book.setAuthorName("John Doe");
        book.setGenre("Programming");
        book.setTitle("Effective Java");
    }

    @Test
    void addBook_ShouldSaveBookAndNotify() {
        bookService.addBook(book);

        verify(repository, times(1)).save(book);
        verify(bookCreatedMessageProducer, times(1)).notifyBookCreated(book.getBookId(), book.getIsbn());
    }

    @Test
    void listBooks_ShouldReturnBookListItems() {
        var book = new Book();
        book.setIsbn("1234567890");

        var books = List.of(book);
        var ordersByIsbn = Map.of("1234567890", 5);
        var inventoryByIsbn = Map.of("1234567890", 10);
        var ratingByIsbn = Map.of("1234567890", 4);

        when(repository.findAll()).thenReturn(books);
        when(orderServiceIntegration.listOrdersForBooks(anyList())).thenReturn(ordersByIsbn);
        when(inventoryServiceIntegration.findInventoryByISBN(anyList())).thenReturn(inventoryByIsbn);
        when(bestReadsServiceIntegration.getScoresByIsbn(anyList())).thenReturn(ratingByIsbn);

        var result = bookService.listBooks();

        var expectedItem = new BookService.BookListItem(book, 5, true, 4);

        assertThat(result).containsExactly(expectedItem);
    }

    @Test
    void listBooks_ShouldReturnEmptyListWhenNoBooks() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        var result = bookService.listBooks();

        assertThat(result).isEmpty();
    }

    @Test
    void testNullBook() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookService.addBook(null))
                .withMessage("Book cannot be null.");
    }

    @Test
    void testInvalidISBN() {
        book.setIsbn("");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookService.addBook(book))
                .withMessage("Invalid ISBN. It must be a 13-digit number.");
    }

    @Test
    void testInvalidPageNumbers() {
        book.setPageNumbers(-1);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookService.addBook(book))
                .withMessage("Invalid page numbers. It must be a positive integer.");
    }

    @Test
    void testEmptyAuthorName() {
        book.setAuthorName("");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookService.addBook(book))
                .withMessage("Invalid author name. It cannot be empty.");
    }

    @Test
    void testAuthorNameWithoutLastName() {
        book.setAuthorName("John");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookService.addBook(book))
                .withMessage("Invalid author name. It must include at least a first and last name.");
    }

    @Test
    void testEmptyGenre() {
        book.setGenre("");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookService.addBook(book))
                .withMessage("Invalid genre. It cannot be empty.");
    }

    @Test
    void testEmptyTitle() {
        book.setTitle("");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookService.addBook(book))
                .withMessage("Invalid title. It cannot be empty.");
    }
}
