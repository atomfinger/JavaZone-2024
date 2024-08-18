package io.github.atomfinger.javazone.bookstore.kafka.add_book_listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.github.atomfinger.javazone.bookstore.service.BookService;

@Component
public class AddBookListener {

    private final BookService bookService;

    public AddBookListener(BookService bookService) {
        this.bookService = bookService;
    }

    @KafkaListener(groupId = "BookstoreService", topics = "bookstore.cmd.add-book.1", containerFactory = "addBookKafkaListenerContainerFactory")
    public void listen(AddBookMessage message) {
        bookService.addBook(message.toBook());
    }
}
