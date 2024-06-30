package io.github.atomfinger.javazone.bookstore.acceptance_test;

import io.github.atomfinger.javazone.bookstore.bookstore.domain.Book;
import org.approvaltests.Approvals;
import org.approvaltests.JsonApprovals;
import org.approvaltests.core.Options;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.util.Assert;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ListBooksTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void given_that_a_book_exists_then_we_should_have_one_listed_out_when_listing_out_books() {
        var result = restTemplate.getForObject("http://localhost:" + port + "/api/books", String.class);
        JsonApprovals.verifyJson(result);
    }
}
