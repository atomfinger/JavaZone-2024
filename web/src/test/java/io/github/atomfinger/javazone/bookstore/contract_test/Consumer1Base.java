package io.github.atomfinger.javazone.bookstore.contract_test;

import io.github.atomfinger.javazone.bookstore.controller.BookController;
import io.restassured.config.EncoderConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import org.junit.jupiter.api.BeforeEach;

public abstract class Consumer1Base {

    BookController controller = new BookController();

    @BeforeEach
    public void setup() {
        var encoderConfig = new EncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false);
        RestAssuredMockMvc.config = new RestAssuredMockMvcConfig().encoderConfig(encoderConfig);
        RestAssuredMockMvc.standaloneSetup(this.controller);
    }

}
