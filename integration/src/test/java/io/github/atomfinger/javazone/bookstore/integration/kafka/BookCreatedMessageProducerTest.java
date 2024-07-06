package io.github.atomfinger.javazone.bookstore.integration.kafka;

import io.github.atomfinger.javazone.bookstore.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BookCreatedMessageProducerTest extends BaseIntegrationTest {


    @Autowired
    KafkaStringConsumer kafkaStringConsumer;
    @Autowired
    BookCreatedMessageProducer producer;

    @Test
    public void given_that_we_have_send_a_new_book_message_then_we_should_be_able_to_read_that_back_again()
            throws Exception {
        producer.notifyBookCreated(2L, "ISBN!");
        var hasReceivedMessage = kafkaStringConsumer.getLatch().await(5, TimeUnit.SECONDS);
        assertThat(hasReceivedMessage).isTrue();
        assertThat(kafkaStringConsumer.getPayload()).isEqualTo("{\"bookId\":2,\"isbn\":\"ISBN!\"}");
    }
}