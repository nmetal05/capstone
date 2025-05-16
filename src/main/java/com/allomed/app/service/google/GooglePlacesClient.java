package com.allomed.app.service.google;

import com.allomed.app.config.ApplicationProperties;
import com.allomed.app.service.google.dto.GoogleNearbySearchResponse;
import com.allomed.app.service.google.dto.GooglePlaceDetailsResponse;
import com.allomed.app.service.google.dto.GoogleSearchTextRequest;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Wraps Google Places "Text Search (v1)" and "Place Details" endpoints.
 */
@Component
public class GooglePlacesClient {

    /* ---------------------------  End-points  --------------------------- */

    private static final String SEARCH_TEXT = "/v1/places:searchText";
    private static final String DETAILS = "/v1/places/%s";

    /* ---------------------------  Field masks  -------------------------- */
    /* Text-Search can return ONLY the leaf fields below.                   */
    private static final String MASK_LIST =
        "places.name,places.displayName,places.formattedAddress," +
        "places.location,places.rating,places.userRatingCount,places.currentOpeningHours,places.regularOpeningHours";

    /* Place-Details may include hours & phone. */
    private static final String MASK_DETAIL =
        "name,displayName,formattedAddress,location," +
        "rating,userRatingCount," +
        "currentOpeningHours,regularOpeningHours,nationalPhoneNumber,googleMapsUri,googleMapsLinks.directionsUri";

    /* -------------------------------------------------------------------- */

    private final WebClient web;

    public GooglePlacesClient(ApplicationProperties props, WebClient.Builder builder) {
        this.web = builder
            .baseUrl("https://places.googleapis.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("X-Goog-Api-Key", props.getExternalPlaces().getApiKey())
            .build();
    }

    /**
     * Executes a Text-Search ranked by DISTANCE inside the given radius (metres).
     */
    public GoogleNearbySearchResponse searchText(String specialization, double lat, double lon, double radiusMeters) {
        /* -------- Build request body ------------------------------------ */

        GoogleSearchTextRequest.Center center = new GoogleSearchTextRequest.Center(lat, lon);
        GoogleSearchTextRequest.Circle circle = new GoogleSearchTextRequest.Circle(center, radiusMeters);
        GoogleSearchTextRequest.LocationBias bias = new GoogleSearchTextRequest.LocationBias(circle);

        GoogleSearchTextRequest body = new GoogleSearchTextRequest(
            specialization + " doctor", // textQuery
            "DISTANCE", // rankPreference
            20, // pageSize  (1-20)
            bias
        ); // locationBias

        /* -------- Call Google ------------------------------------------- */

        try {
            return web
                .post()
                .uri(SEARCH_TEXT)
                .header("X-Goog-FieldMask", MASK_LIST)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(GoogleNearbySearchResponse.class)
                .timeout(Duration.ofSeconds(6))
                .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            // show Google's error message in logs, then re-throw
            System.err.println("Google Places searchText failed -> " + e.getResponseBodyAsString());
            throw e;
        }
    }

    /** Retrieves place details (phone, hours, …) for a single Place-ID. */
    public GooglePlaceDetailsResponse details(String placeId) {
        return web
            .get()
            .uri(DETAILS.formatted(placeId))
            .header("X-Goog-FieldMask", MASK_DETAIL)
            .retrieve()
            .bodyToMono(GooglePlaceDetailsResponse.class)
            .timeout(Duration.ofSeconds(6))
            .block();
    }
}
