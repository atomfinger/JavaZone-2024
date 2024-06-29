package io.github.atomfinger.javazone.bookstore.bookstore.domain;

public class Book {

    private String title, description, author;

    public Book(String title, String description, String author) {
        this.title = title;
        this.description = description;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }
}
