package io.github.atomfinger.javazone.bookstore.acceptance_test.tests;

import io.github.atomfinger.javazone.bookstore.acceptance_test.AcceptanceTestBase;
import io.github.atomfinger.javazone.bookstore.bookstore.persistence.entities.Book;
import io.github.atomfinger.javazone.bookstore.bookstore.persistence.repository.BookRepository;
import org.approvaltests.JsonApprovals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class ListBooksTest extends AcceptanceTestBase {

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        var book = new Book();
        book.setTitle("Effective Java");
        book.setDescription("A comprehensive guide to programming in Java.");
        book.setIsbn("9780134685991");
        book.setAuthorName("Joshua Bloch");
        book.setPageNumbers(416);
        book.setPublishedDate(new GregorianCalendar(2018, Calendar.JANUARY, 6).getTime());
        book.setGenre("Programming");
        bookRepository.save(book);
        mockServerClient.when(request())
                .respond(
                        response()
                                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                .withStatusCode(200)
                        .withBody("""
                                { "data": [{"isbn": {"code":"9780134685991"}, "order_count": 3}] }
                                """)
                );
    }

    @Test
    void given_that_a_book_exists_then_we_should_have_one_listed_out_when_listing_out_books() {
        var result = restTemplate.getForObject(getUrl() + "/books", String.class);
        JsonApprovals.verifyJson(result);
    }
}
