package io.github.atomfinger.javazone.bookstore.acceptance_test.tests;

import io.github.atomfinger.javazone.bookstore.acceptance_test.AcceptanceTestBase;
import io.github.atomfinger.javazone.bookstore.persistence.entities.Book;
import io.github.atomfinger.javazone.bookstore.persistence.repository.BookRepository;
import org.approvaltests.JsonApprovals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.mockserver.model.HttpRequest.request;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class ListBooksTest extends AcceptanceTestBase {

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        bookRepository.save(getBook());
        createExpectation("order", """
                { "data": [{"isbn": {"code":"9780134685991"}, "order_count": 3}] }
                """);
        createExpectation("inventory", """
                {"inventory": [{"isbn": "9780134685991","count": 5}]}
                """);
        createExpectation("best-reads", """
                {"reviews": [{"isbn": "9780134685991","score": 3}]}
                """);
    }

    @Test
    void given_that_a_book_exists_then_we_should_have_one_listed_out_when_listing_out_books() {
        var result = restTemplate.getForObject(getUrl() + "/books", String.class);
        JsonApprovals.verifyJson(result);
    }

    private void createExpectation(String urlIdentifier, String body) {
        mockServerClient.when(request().withPath(".*" + urlIdentifier + "/.*")).respond(response(body));
    }

    private HttpResponse response(String body) {
        return HttpResponse.response()
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withStatusCode(200)
                .withBody(body);
    }

    private static Book getBook() {
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
