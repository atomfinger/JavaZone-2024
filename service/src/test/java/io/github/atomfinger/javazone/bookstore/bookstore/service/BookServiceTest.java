package io.github.atomfinger.javazone.bookstore.bookstore.service;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.entities.Book;
import io.github.atomfinger.javazone.bookstore.bookstore.persistence.repository.BookRepository;
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
        book.setBookId(1L);
        book.setIsbn("1234567890");
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
}