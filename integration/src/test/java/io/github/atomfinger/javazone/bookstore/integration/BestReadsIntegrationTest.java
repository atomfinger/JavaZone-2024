package io.github.atomfinger.javazone.bookstore.integration;

import org.junit.jupiter.api.Test;
import org.mockserver.model.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * This is an example of a test where we integrate with a service that does not have:
 * - OpenAPI
 * - A stable test environment we can test against
 * - Contract tests
 * <p>
 * This is the least preferred way to test and other options should be considered before this.
 * <p>
 * It
 */
class BestReadsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    BestReadsIntegration bestReadsIntegration;

    @Test
    public void when_asking_for_review_scores_then_we_should_be_able_to_read_the_result() {
        mockServerClient.when(request().withPath("/api/scores")
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withBody(new JsonBody("""
                                {"isbns" : [ "9780142424179", "9780765326355", "9780061120084" ]}
                                """)))
                .respond(response().withStatusCode(200)
                        .withHeader(CONTENT_TYPE, "application/json; charset=utf-8")
                        .withBody("""
                                { "reviews": [
                                {"isbn": "9780142424179", "score": 1},
                                {"isbn": "9780765326355", "score": 5},
                                {"isbn": "9780061120084", "score": 0}
                                ] }
                                """));

        var result = bestReadsIntegration.getScoresByIsbn(List.of("9780142424179", "9780765326355", "9780061120084"));
        assertThat(result).containsEntry("9780142424179", 1)
                .containsEntry("9780765326355", 5)
                .containsEntry("9780061120084", 0)
                .hasSize(3);
    }
}