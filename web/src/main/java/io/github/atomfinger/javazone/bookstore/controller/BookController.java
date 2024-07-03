package io.github.atomfinger.javazone.bookstore.controller;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.atomfinger.javazone.bookstore.bookstore.service.BookService;
import io.github.atomfinger.javazone.bookstore.bookstore.service.BookService.BookWithOrderNumbers;

@RestController
@RequestMapping("api/books")
public class BookController {

  private final BookService service;

  public BookController(BookService service) {
    this.service = service;
  }

  @GetMapping
  @ResponseBody
  public List<BookResponse> listBooks() {
    return service.listBooks().stream().map(book -> map(book)).toList();
  }

  private static BookResponse map(BookWithOrderNumbers bookWithOrderNumbers) {
    var book = bookWithOrderNumbers.book();
    return new BookResponse(
        book.getBookId(),
        book.getTitle(),
        book.getDescription(),
        book.getIsbn(),
        book.getAuthorName(),
        book.getPageNumbers(),
        book.getPublishedDate(),
        book.getGenre(),
        bookWithOrderNumbers.orderNumber());
  }

  public record BookResponse(
      Long bookId,
      String title,
      String description,
      String isbn,
      String authorName,
      Integer pageNumbers,
      Date publishedDate,
      String genre,
      Integer numberOfOrders) {
  }
}
