package com.allomed.app.service.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Record for the Place-Details response (field mask defined in client). */
public record GooglePlaceDetailsResponse(
    @JsonProperty("name") String placeId,
    @JsonProperty("displayName") DisplayName displayName,
    String formattedAddress,
    Location location,
    Double rating,
    @JsonProperty("userRatingCount") Integer userRatingCount,
    @JsonProperty("currentOpeningHours") CurrentHours current,
    String nationalPhoneNumber,
    String googleMapsUri,
    @JsonProperty("googleMapsLinks") GoogleMapsLinks googleMapsLinks
) {
    public record DisplayName(String text) {}
    public record Location(Double latitude, Double longitude) {}
    public record CurrentHours(Boolean openNow, List<String> weekdayDescriptions) {}
    public record GoogleMapsLinks(String directionsUri) {}
}
