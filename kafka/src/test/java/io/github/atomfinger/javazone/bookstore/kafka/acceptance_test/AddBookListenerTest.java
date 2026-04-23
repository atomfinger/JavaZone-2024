package io.github.atomfinger.javazone.bookstore.kafka.acceptance_test;

import io.github.atomfinger.javazone.bookstore.kafka.acceptance_test.consumer.KafkaStringConsumer;
import io.github.atomfinger.javazone.bookstore.kafka.add_book_listener.AddBookMessage;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import org.approvaltests.JsonApprovals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.approvaltests.namer.NamerFactory.asMachineSpecificTest;
import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

class AddBookListenerTest extends AcceptanceTestBase {

    @Autowired
    KafkaStringConsumer consumer;
    @Autowired
    JsonMapper jsonMapper;

    @Test
    public void given_that_we_add_a_new_book_then_new_book_should_be_added_to_db() throws InterruptedException, JacksonException {
        sendMessage();
        var result = bookRepository.findAll().iterator().next();
        asMachineSpecificTest(() -> "book_stored_in_db");
        JsonApprovals.verifyJson(jsonMapper.writeValueAsString(result));
        await().atMost(10, SECONDS).until(() -> consumer.getPayload() != null);
        asMachineSpecificTest(() -> "message_sent_to_kafka");
        JsonApprovals.verifyJson(consumer.getPayload());
    }

    private void sendMessage() throws InterruptedException {
        kafkaTemplate().send("bookstore.cmd.add-book.1", "key", createMessage());
        await().atMost(30, SECONDS).untilAsserted(() -> {
            assertThat(bookRepository.count()).isGreaterThan(0L);
        });
    }

    private static AddBookMessage createMessage() {
        return new AddBookMessage(2L,
                "Effective Java",
                "A comprehensive guide to programming in Java.",
                "9780134685992",
                "Joshua Bloch",
                416,
                LocalDate.of(2018, 1, 6),
                "Programming");

    }
}
