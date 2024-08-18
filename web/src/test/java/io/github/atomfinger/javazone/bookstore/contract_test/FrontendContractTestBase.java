package io.github.atomfinger.javazone.bookstore.contract_test;

import io.github.atomfinger.javazone.bookstore.persistence.entities.Book;
import io.github.atomfinger.javazone.bookstore.service.BookService;
import io.github.atomfinger.javazone.bookstore.service.BookService.BookListItem;
import io.github.atomfinger.javazone.bookstore.controller.BookController;
import io.restassured.config.EncoderConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.List;

public abstract class FrontendContractTestBase {

    BookController controller;

    @BeforeEach
    public void setup() {
        controller = new BookController(setupMockService());
        setupMockMvc();
    }

    private void setupMockMvc() {
        var encoderConfig = new EncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false);
        RestAssuredMockMvc.config = new RestAssuredMockMvcConfig().encoderConfig(encoderConfig);
        RestAssuredMockMvc.standaloneSetup(this.controller);
    }

    private BookService setupMockService() {
        var book = new Book();
        book.setTitle("Test");
        book.setDescription("More test");
        book.setAuthorName("John");
        var bookWithOrderNums = new BookListItem(book, null, true, 6);
        var mockedBookService = Mockito.mock(BookService.class);
        Mockito.when(mockedBookService.listBooks()).thenReturn(List.of(bookWithOrderNums));
        return mockedBookService;
    }
}
