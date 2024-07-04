package io.github.atomfinger.javazone.bookstore.controller;

import io.github.atomfinger.javazone.bookstore.bookstore.service.BookService;
import io.github.atomfinger.javazone.bookstore.bookstore.service.BookService.BookListItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    private static BookResponse map(BookListItem bookListItem) {
        var book = bookListItem.book();
        return new BookResponse(
                book.getBookId(),
                book.getTitle(),
                book.getDescription(),
                book.getIsbn(),
                book.getAuthorName(),
                book.getPageNumbers(),
                book.getPublishedDate(),
                book.getGenre(),
                bookListItem.orderNumber(),
                bookListItem.isInStock(),
                bookListItem.rating()
        );
    }

    @GetMapping
    @ResponseBody
    public List<BookResponse> listBooks() {
        return service.listBooks().stream().map(BookController::map).toList();
    }

    public record BookResponse(Long bookId, String title, String description, String isbn, String authorName,
                               Integer pageNumbers, Date publishedDate, String genre, Integer numberOfOrders,
                               boolean isInStock, Integer rating) {
    }
}
