package com.allomed.app.service.google;

import com.allomed.app.service.dto.DoctorDetailsDTO;
import com.allomed.app.service.dto.DoctorNearbyDTO;
import com.allomed.app.service.google.dto.GoogleNearbySearchResponse;
import com.allomed.app.service.google.dto.GooglePlaceDetailsResponse;
import com.allomed.app.service.google.dto.RouteMatrixElement;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GooglePlacesService {

    private static String barePlaceId(String name) {
        return (name != null && name.startsWith("places/")) ? name.substring("places/".length()) : name;
    }

    private static int googleDurationToSeconds(String googleDuration) {
        if (googleDuration == null || googleDuration.isBlank()) return 0;
        // strip the trailing 's', parse as double, round to nearest second
        double seconds = Double.parseDouble(googleDuration.substring(0, googleDuration.length() - 1));
        return (int) Math.round(seconds);
    }

    private final GooglePlacesClient places;
    private final GoogleRoutesClient routes;

    /**
     * Combines Text-search + Routes matrix, supports filtering & sorting.
     */
    public List<DoctorNearbyDTO> findNearby(
        String specialization,
        double lat,
        double lon,
        double radiusKm,
        String sort,
        String dir,
        boolean openNowOnly
    ) {
        GoogleNearbySearchResponse resp = places.searchText(specialization, lat, lon, radiusKm * 1000);

        if (resp == null || resp.places() == null) return Collections.emptyList();

        final List<DoctorNearbyDTO> populatedDtoList = resp
            .places()
            .stream()
            .map(p -> {
                DoctorNearbyDTO d = new DoctorNearbyDTO();
                d.setProvider("GOOGLE");

                // ---- identifiers & basic props ----
                d.setPlaceId(barePlaceId(p.name()));
                d.setName(p.displayName().text());
                d.setAddress(p.formattedAddress());
                d.setLatitude(p.location().latitude());
                d.setLongitude(p.location().longitude());

                // ---- reputation ----
                d.setRating(p.rating());
                d.setUserRatingCount(p.userRatingCount());

                // ---- opening hours ----
                if (p.current() != null) {
                    d.setOpenNow(p.current().openNow());
                    d.setWeekdayDescriptions(p.current().weekdayDescriptions());
                } else if (p.regular() != null) {
                    d.setWeekdayDescriptions(p.regular().weekdayDescriptions());
                }
                return d;
            })
            .toList();

        if (populatedDtoList.isEmpty()) {
            return Collections.emptyList();
        }

        // --- Use Routes-API to get distance & travel time -------------------
        List<String> ids = populatedDtoList.stream().map(DoctorNearbyDTO::getPlaceId).map(GooglePlacesService::barePlaceId).toList();

        List<RouteMatrixElement> matrix = routes.compute(lat, lon, ids);

        matrix.forEach(el -> {
            String statusStr = el.status() != null
                ? String.format("code=%d message='%s'", el.status().code(), el.status().message())
                : "null";

            System.out.printf(
                "ROUTE: index=%d | status=%s | condition=%s | distance=%s | duration=%s | origin=%s -> destination=%s%n",
                el.destinationIndex(),
                statusStr,
                el.condition(),
                el.distanceMeters(),
                el.duration(),
                el.originIndex(),
                el.destinationIndex()
            );

            if (el.destinationIndex() >= 0 && el.destinationIndex() < populatedDtoList.size()) {
                DoctorNearbyDTO dto = populatedDtoList.get(el.destinationIndex());

                // Populate distance and travel time if the API provided them and
                // did not report a specific error for this route matrix element.
                // A null el.status() indicates success or no specific error for this element from the Routes API.
                if (el.status() == null) {
                    if (el.distanceMeters() != null) {
                        dto.setDistanceKm(el.distanceMeters() / 1000.0);
                    }
                    // Only set travel duration if a valid duration string is provided by the API
                    if (el.duration() != null && !el.duration().isBlank()) {
                        dto.setTravelDurationSec(googleDurationToSeconds(el.duration()));
                    }
                } else {
                    // An error status was reported for this element by the Routes API.
                    // Log this information and do not use potentially unreliable distance/duration values.
                    System.out.printf(
                        "INFO: Route details for destination index %d (placeId: %s) not populated due to API error status for this route leg: code=%d, message='%s'. Route condition was: %s%n",
                        el.destinationIndex(),
                        dto.getPlaceId(), // Assuming getPlaceId() is available on dto
                        el.status().code(),
                        el.status().message(),
                        el.condition()
                    );
                }
            }
        });

        List<DoctorNearbyDTO> resultList = populatedDtoList;

        // --- Optional filter ------------------------------------------------
        if (openNowOnly) {
            resultList = resultList.stream().filter(d -> d.getOpenNow() != null && d.getOpenNow()).toList();
        }

        if (resultList.isEmpty()) {
            return Collections.emptyList();
        }

        // --- Sorting --------------------------------------------------------
        Comparator<DoctorNearbyDTO> cmp =
            switch (sort.toLowerCase()) {
                case "name" -> Comparator.comparing(DoctorNearbyDTO::getName, String.CASE_INSENSITIVE_ORDER);
                case "rating" -> Comparator.comparing(DoctorNearbyDTO::getRating, Comparator.nullsLast(Double::compareTo));
                default -> Comparator.comparing(DoctorNearbyDTO::getDistanceKm, Comparator.nullsLast(Double::compareTo));
            };
        if ("desc".equalsIgnoreCase(dir)) {
            cmp = cmp.reversed();
        }

        List<DoctorNearbyDTO> sortableList = new ArrayList<>(resultList);
        sortableList.sort(cmp);

        return sortableList;
    }

    /* --------------------------------------------------------- */
    /* -------------------  Place details  --------------------- */
    /* --------------------------------------------------------- */
    public DoctorDetailsDTO getDetails(String placeId) {
        GooglePlaceDetailsResponse r = places.details(placeId);
        if (r == null) {
            return null;
        }

        DoctorDetailsDTO d = new DoctorDetailsDTO();
        d.setPlaceId(r.placeId());
        d.setName(r.displayName().text());
        d.setAddress(r.formattedAddress());
        d.setLatitude(r.location().latitude());
        d.setLongitude(r.location().longitude());
        d.setRating(r.rating());
        d.setUserRatingCount(r.userRatingCount());

        if (r.current() != null) {
            d.setOpenNow(r.current().openNow());
            d.setWeekdayDescriptions(r.current().weekdayDescriptions());
        }
        d.setNationalPhoneNumber(r.nationalPhoneNumber());
        d.setGoogleMapsUri(r.googleMapsUri());
        if (r.googleMapsLinks() != null && r.googleMapsLinks().directionsUri() != null) {
            d.setDirectionsUri(r.googleMapsLinks().directionsUri());
        }
        return d;
    }
}
