package io.github.atomfinger.javazone.bookstore.bookstore.persistence.repository;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.BaseDatabaseIntegrationTest;
import io.github.atomfinger.javazone.bookstore.bookstore.persistence.entities.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

public class BookRepositoryTest extends BaseDatabaseIntegrationTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    public void verify_that_we_can_persist_an_read_books() {
        bookRepository.save(getBook());
        var book = bookRepository.findAll().iterator().next();
        assertThat(book).satisfies(actual -> {
            assertThat(actual).usingRecursiveComparison().ignoringFields("bookId").isEqualTo(getBook());
            assertThat(book.getBookId()).isNotNull();
        });
    }

    private Book getBook() {
        var book = new Book();
        book.setTitle("Effective Java");
        book.setDescription("A comprehensive guide to programming in Java.");
        book.setIsbn("9780134685991");
        book.setAuthorName("Joshua Bloch");
        book.setPageNumbers(416);
        book.setPublishedDate(new GregorianCalendar(2018, Calendar.JANUARY, 6).getTime());
        book.setGenre("Programming");
        return book;
    }
}