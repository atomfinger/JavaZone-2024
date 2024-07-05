package io.github.atomfinger.javazone.bookstore.kafka.acceptance_test;

import io.github.atomfinger.javazone.bookstore.kafka.add_book_listener.AddBookMessage;
import org.approvaltests.JsonApprovals;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class AddBookListenerTest extends AcceptanceTestBase {

    @Test
    public void given_that_we_add_a_new_book_then_new_book_should_be_added_to_db() {
        var input = new AddBookMessage(
                1L,
                "Effective Java",
                "A comprehensive guide to programming in Java.",
                "9780134685991",
                "Joshua Bloch",
                416,
                Date.valueOf("2018-01-06"),
                "Programming"
        );
        kafkaTemplate().send("bookstore.cmd.add-book.1", "key", input);
        await().atMost(5, SECONDS).until(() -> bookRepository.count() > 0);
        var result = bookRepository.findAll().iterator().next();
        JsonApprovals.verifyAsJson(result);
    }
}