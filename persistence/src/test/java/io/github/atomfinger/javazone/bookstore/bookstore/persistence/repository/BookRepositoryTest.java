package io.github.atomfinger.javazone.bookstore.bookstore.persistence.repository;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.BaseDatabaseIntegrationTest;
import io.github.atomfinger.javazone.bookstore.bookstore.persistence.entities.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class BookRepositoryTest extends BaseDatabaseIntegrationTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    public void canInsertBook(){
        var book = new Book();
        book.setTitle("Effective Java");
        book.setDescription("A comprehensive guide to programming in Java.");
        book.setIsbn("9780134685991");
        book.setAuthorName("Joshua Bloch");
        book.setPageNumbers(416);
        book.setPublishedDate(new GregorianCalendar(2018, Calendar.JANUARY, 6).getTime());
        book.setGenre("Programming");
        bookRepository.save(book);
    }
}