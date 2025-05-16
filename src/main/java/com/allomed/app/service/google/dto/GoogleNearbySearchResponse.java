package com.allomed.app.service.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record GoogleNearbySearchResponse(List<Place> places) {
    public record Place(
        String name, // canonical id
        @JsonProperty("displayName") DisplayName displayName,
        String formattedAddress,
        Location location,
        Double rating,
        Integer userRatingCount,
        @JsonProperty("currentOpeningHours") OpeningHours current,
        @JsonProperty("regularOpeningHours") OpeningHours regular
    ) {}

    public record DisplayName(String text, String languageCode) {}

    public record Location(Double latitude, Double longitude) {}

    public record OpeningHours(Boolean openNow, List<String> weekdayDescriptions) {}
}
