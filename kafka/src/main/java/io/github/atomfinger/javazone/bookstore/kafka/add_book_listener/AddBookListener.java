package io.github.atomfinger.javazone.bookstore.kafka.add_book_listener;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.repository.BookRepository;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(groupId = "BookstoreService", topics = "bookstore.cmd.add-book.1")
public class AddBookListener {

    private final BookRepository bookRepository;

    public AddBookListener(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @KafkaHandler
    public void listen(AddBookMessage message) {
        bookRepository.save(message.toBook());
    }
}
