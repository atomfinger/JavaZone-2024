package io.github.atomfinger.javazonee.bookstore.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.github.atomfinger.javazone.bookstore.persistence.entities.Book;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);
}
