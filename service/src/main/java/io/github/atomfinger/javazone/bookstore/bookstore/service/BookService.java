package io.github.atomfinger.javazone.bookstore.bookstore.service;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.entities.Book;
import io.github.atomfinger.javazone.bookstore.bookstore.persistence.repository.BookRepository;
import io.github.atomfinger.javazone.bookstore.integration.OrderServiceIntegration;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

  private final BookRepository repository;
  private final OrderServiceIntegration orderServiceIntegration;

  public BookService(BookRepository repository, OrderServiceIntegration orderServiceIntegration) {
    this.repository = repository;
    this.orderServiceIntegration = orderServiceIntegration;
  }

  public List<BookWithOrderNumbers> listBooks() {
    var books = ((List<Book>) repository.findAll());
    var isbns = books.stream().map(book -> book.getIsbn()).toList();
    var ordersByIsbns = orderServiceIntegration.listOrdersForBooks(isbns);
    return books.stream().map(book -> new BookWithOrderNumbers(book, ordersByIsbns.get(book.getIsbn()))).toList();
  }

  public record BookWithOrderNumbers(Book book, Integer orderNumber) {
  }
}
