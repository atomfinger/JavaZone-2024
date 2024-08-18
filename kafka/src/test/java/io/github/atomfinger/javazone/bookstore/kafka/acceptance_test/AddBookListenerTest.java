package io.github.atomfinger.javazone.bookstore.kafka.acceptance_test;

import io.github.atomfinger.javazone.bookstore.kafka.acceptance_test.consumer.KafkaStringConsumer;
import io.github.atomfinger.javazone.bookstore.kafka.add_book_listener.AddBookMessage;
import org.approvaltests.JsonApprovals;
import org.approvaltests.namer.NamerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class AddBookListenerTest extends AcceptanceTestBase {

    @Autowired
    KafkaStringConsumer consumer;

    @Test
    public void given_that_we_add_a_new_book_then_new_book_should_be_added_to_db() throws InterruptedException {
        sendMessage();
        var result = bookRepository.findAll().iterator().next();
        NamerFactory.asMachineSpecificTest(() -> "book_stored_in_db");
        JsonApprovals.verifyAsJson(result);
        NamerFactory.asMachineSpecificTest(() -> "message_sent_to_kafka");
        JsonApprovals.verifyJson(consumer.getPayload());
    }

    private void sendMessage() throws InterruptedException {
        kafkaTemplate().send("bookstore.cmd.add-book.1", "key", createMessage());
        await().atMost(20, SECONDS).until(() -> bookRepository.count() > 0);
        assertThat(consumer.getLatch().await(20, SECONDS)).isTrue();
    }

    private static AddBookMessage createMessage() {
        return new AddBookMessage(
                1L,
                "Effective Java",
                "A comprehensive guide to programming in Java.",
                "9780134685991",
                "Joshua Bloch",
                416,
                Date.valueOf("2018-01-06"),
                "Programming");

    }
}
