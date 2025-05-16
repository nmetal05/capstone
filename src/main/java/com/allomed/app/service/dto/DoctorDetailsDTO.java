package com.allomed.app.service.dto;

import java.util.List;
import lombok.Data;

/**
 * Detailed view returned by
 *   GET /api/doctors/google/{placeId}/detail
 */
@Data
public class DoctorDetailsDTO {

    private String provider = "GOOGLE";
    private String placeId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;

    // Reputation
    private Double rating;
    private Integer userRatingCount;

    // Opening hours
    private Boolean openNow; // from currentOpeningHours
    private List<String> weekdayDescriptions; // Mon 9-17, Tue …

    // Contact
    private String nationalPhoneNumber; // local format only
    private String googleMapsUri;
    private String directionsUri; // New field for directions URI
}
