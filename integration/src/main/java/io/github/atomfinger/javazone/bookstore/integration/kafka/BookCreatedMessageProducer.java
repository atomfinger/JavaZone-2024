package io.github.atomfinger.javazone.bookstore.integration.kafka;

import io.github.atomfinger.javazone.bookstore.integration.kafka.models.BookCreated;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookCreatedMessageProducer {

    private final KafkaTemplate<String, BookCreated> kafkaTemplate;

    public BookCreatedMessageProducer(KafkaTemplate<String, BookCreated> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void notifyBookCreated(Long bookId, String isbn) {
        kafkaTemplate.send("bookstore.fct.book-added.1", bookId.toString(), new BookCreated(bookId, isbn));
    }
}
