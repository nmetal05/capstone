package com.allomed.app.service.google;

import com.allomed.app.config.ApplicationProperties;
import com.allomed.app.service.google.dto.RouteMatrixElement;
import java.time.Duration;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GoogleRoutesClient {

    private static final String URL = "/distanceMatrix/v2:computeRouteMatrix";
    private static final String MASK = "originIndex,destinationIndex,distanceMeters,duration";

    private final WebClient web;

    public GoogleRoutesClient(ApplicationProperties props, WebClient.Builder builder) {
        this.web = builder
            .baseUrl("https://routes.googleapis.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("X-Goog-Api-Key", props.getExternalPlaces().getApiKey())
            .defaultHeader("X-Goog-FieldMask", MASK)
            .build();
    }

    public List<RouteMatrixElement> compute(double originLat, double originLon, List<String> placeIds) {
        String destJson = placeIds
            .stream()
            .map(id -> "{\"waypoint\":{\"placeId\":\"" + id + "\"}}")
            .reduce((a, b) -> a + "," + b)
            .orElse("");

        String body =
            """
            {
              "origins":[{"waypoint":{"location":{"latLng":{"latitude":%f,"longitude":%f}}}}],
              "destinations":[%s],
              "travelMode":"DRIVE",
              "routingPreference":"TRAFFIC_AWARE"
            }""".formatted(originLat, originLon, destJson);

        return web
            .post()
            .uri(URL)
            .bodyValue(body)
            .retrieve()
            .bodyToFlux(RouteMatrixElement.class)
            .collectList()
            .timeout(Duration.ofSeconds(6))
            .block();
    }
}
