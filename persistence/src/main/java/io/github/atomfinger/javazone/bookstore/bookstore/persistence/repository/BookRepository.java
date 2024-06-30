package io.github.atomfinger.javazone.bookstore.bookstore.persistence.repository;

import io.github.atomfinger.javazone.bookstore.bookstore.persistence.entities.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
}
