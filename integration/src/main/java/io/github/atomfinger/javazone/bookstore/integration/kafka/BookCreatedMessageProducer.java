package io.github.atomfinger.javazone.bookstore.integration.kafka;

import io.github.atomfinger.javazone.bookstore.integration.kafka.models.BookCreated;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
