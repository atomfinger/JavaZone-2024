package io.github.atomfinger.javazone.bookstore.controller;

import io.github.atomfinger.javazone.bookstore.bookstore.domain.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/books")
public class BookController {

    @GetMapping
    @ResponseBody
    public List<Book> listBooks() {
        return List.of(new Book("Test", "More test", "John"));
    }
}
