package io.github.atomfinger.javazone.bookstore.controller;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.entities.Book;
import io.github.atomfinger.javazone.bookstore.bookstore.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseBody
    public List<Book> listBooks() {
        return service.listBooks();
    }
}
