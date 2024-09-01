package io.github.atomfinger.javazone.bookstore.kafka.add_book_listener;

import io.github.atomfinger.javazone.bookstore.persistence.entities.Book;

import java.util.Date;

public record AddBookMessage(Long bookId, String title, String description, String isbn, String authorName,
        Integer pageNumbers, Date publishedDate, String genre) {

    public Book toBook() {
        var book = new Book();
        book.setBookId(bookId);
        book.setTitle(title);
        book.setDescription(description);
        book.setIsbn(isbn);
        book.setAuthorName(authorName);
        book.setPageNumbers(pageNumbers);
        book.setPublishedDate(publishedDate);
        book.setGenre(genre);
        return book;
    }

}
