package io.github.atomfinger.javazone.bookstore.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.atomfinger.javazone.bookstore.persistence.BaseDatabaseIntegrationTest;
import io.github.atomfinger.javazone.bookstore.persistence.entities.Book;

public class BookRepositoryTest extends BaseDatabaseIntegrationTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    public void verify_that_we_can_persist_an_read_books() {
        bookRepository.save(getBook());
        var book = bookRepository.findAll().iterator().next();
        assertThat(book).satisfies(actual -> {
            assertThat(actual).hasNoNullFieldsOrProperties(); // Verifies that we test all the fields in the class
            assertThat(actual).usingRecursiveComparison().ignoringFields("bookId").isEqualTo(getBook());
            assertThat(book.getBookId()).isNotNull();
        });
    }

    @Test
    public void verify_that_we_can_read_book_by_isbn() {
        bookRepository.save(getBook());
        assertThat(bookRepository.findByIsbn("9780134685991")).isNotEmpty();
    }

    private Book getBook() {
        var book = new Book();
        book.setTitle("Effective Java");
        book.setDescription("A comprehensive guide to programming in Java.");
        book.setIsbn("9780134685991");
        book.setAuthorName("Joshua Bloch");
        book.setPageNumbers(416);
        book.setPublishedDate(LocalDate.of(2018, 1, 6));
        book.setGenre("Programming");
        return book;
    }
}
