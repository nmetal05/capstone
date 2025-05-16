package com.allomed.app.service.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// Using records for conciseness and immutability
public record GoogleSearchTextRequest(
    @JsonProperty("textQuery") String textQuery,
    @JsonProperty("rankPreference") String rankPreference,
    @JsonProperty("pageSize") int pageSize, // <- new field
    @JsonProperty("locationBias") LocationBias locationBias // <- rename
) {
    public record LocationBias(@JsonProperty("circle") Circle circle) {}

    public record Circle(@JsonProperty("center") Center center, @JsonProperty("radius") double radiusMeters) {}

    public record Center(@JsonProperty("latitude") double latitude, @JsonProperty("longitude") double longitude) {}
}
