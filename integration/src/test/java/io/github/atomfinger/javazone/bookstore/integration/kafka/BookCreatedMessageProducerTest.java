package io.github.atomfinger.javazone.bookstore.integration.kafka;

import io.github.atomfinger.javazone.bookstore.integration.BaseIntegrationTest;
import io.github.atomfinger.javazone.bookstore.integration.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BookCreatedMessageProducerTest extends BaseIntegrationTest {


    @Autowired
    BookCreatedTestConsumer bookCreatedTestConsumer;
    @Autowired
    BookCreatedMessageProducer producer;

    @Test
    public void given_that_we_have_send_a_new_book_message_then_we_should_be_able_to_read_that_back_again()
            throws Exception {
        producer.notifyBookCreated(2L, "ISBN!");
        var hasReceivedMessage = bookCreatedTestConsumer.getLatch().await(5, TimeUnit.SECONDS);
        assertThat(hasReceivedMessage).isTrue();
        assertThat(bookCreatedTestConsumer.getPayload()).isEqualTo("{\"bookId\":2,\"isbn\":\"ISBN!\"}");
    }
}