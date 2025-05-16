package com.allomed.app.service.dto;

import java.util.List;
import lombok.Data;

/**
 * Lightweight card used by GET /api/doctors/nearby
 */
@Data
public class DoctorNearbyDTO {

    private String provider; // "GOOGLE" (later "LOCAL")
    private String placeId; // Google placeId
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;

    private Double distanceKm; // filled by Routes API
    private Integer travelDurationSec; // seconds, from Routes

    private Double rating;
    private Integer userRatingCount;

    private Boolean openNow;
    private List<String> weekdayDescriptions;
}
