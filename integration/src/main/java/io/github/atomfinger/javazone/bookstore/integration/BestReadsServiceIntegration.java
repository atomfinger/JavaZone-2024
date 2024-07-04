package io.github.atomfinger.javazone.bookstore.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.atomfinger.javazone.bookstore.integration.configuration.ApiConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BestReadsServiceIntegration {

    String bestReadsEndpoint;

    public BestReadsServiceIntegration(ApiConfiguration apiConfiguration) {
        bestReadsEndpoint = apiConfiguration.getBestReadsEndpoint();
    }

    public Map<String, Integer> getScoresByIsbn(List<String> isbns) {
        var body = new BestReadsRequest(isbns).toJson();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var request = new HttpEntity<>(body, headers);
        var response = new RestTemplate().postForObject(
                bestReadsEndpoint + "/api/scores",
                request,
                BestReadsReviewResponse.class
        );
        return response.reviews.stream().collect(Collectors.toMap(x -> x.isbn, x -> x.score));
    }

    public record BestReadsReviewResponse(List<BestReadsReview> reviews) {
    }

    public record BestReadsReview(String isbn, Integer score) {
    }

    private record BestReadsRequest(List<String> isbns) {

        public String toJson() {
            try {
                return new ObjectMapper().writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
