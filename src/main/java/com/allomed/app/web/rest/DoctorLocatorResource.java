package com.allomed.app.web.rest;

import com.allomed.app.service.DoctorProfileQueryService;
import com.allomed.app.service.DoctorProfileService;
import com.allomed.app.service.criteria.DoctorProfileCriteria;
import com.allomed.app.service.dto.DoctorDetailsDTO;
import com.allomed.app.service.dto.DoctorNearbyDTO;
import com.allomed.app.service.dto.DoctorProfileDTO;
import com.allomed.app.service.google.GooglePlacesService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.StringFilter;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorLocatorResource {

    private final GooglePlacesService googleService;
    private final DoctorProfileService doctorProfileService;
    private final DoctorProfileQueryService doctorProfileQueryService;

    /**
     * Hybrid endpoint that combines local doctors from database with Google Places results
     *
     * Example:
     * GET /api/doctors/nearby?spec=cardiology&lat=33.51&lon=-7.64&radius=10&sort=distance&dir=asc&openNow=false
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<DoctorNearbyDTO>> nearbyHybrid(
        @RequestParam String spec,
        @RequestParam double lat,
        @RequestParam double lon,
        @RequestParam(defaultValue = "10") double radius,
        @RequestParam(defaultValue = "distance") String sort,
        @RequestParam(defaultValue = "asc") String dir,
        @RequestParam(defaultValue = "false") boolean openNow
    ) {
        List<DoctorNearbyDTO> allDoctors = new ArrayList<>();

        // 1. Get local doctors from database
        List<DoctorNearbyDTO> localDoctors = getLocalDoctors(spec, lat, lon, radius);
        allDoctors.addAll(localDoctors);

        // 2. Get Google doctors
        List<DoctorNearbyDTO> googleDoctors = googleService.findNearby(spec, lat, lon, radius, sort, dir, openNow);
        allDoctors.addAll(googleDoctors);

        // 3. Sort combined results
        sortDoctors(allDoctors, sort, dir);

        return allDoctors.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(allDoctors);
    }

    /**
     * List nearby doctors by specialization, radius & optional sorting (Google only).
     *
     * Example:
     * GET /api/doctors/google/nearby?spec=cardiology&lat=33.51&lon=-7.64&radiusKm=3
     */
    @GetMapping("/google/nearby")
    public ResponseEntity<List<DoctorNearbyDTO>> nearby(
        @RequestParam String spec,
        @RequestParam double lat,
        @RequestParam double lon,
        @RequestParam(defaultValue = "5") double radiusKm,
        @RequestParam(defaultValue = "distance") String sort,
        @RequestParam(defaultValue = "asc") String dir,
        @RequestParam(defaultValue = "false") boolean openNowOnly
    ) {
        List<DoctorNearbyDTO> list = googleService.findNearby(spec, lat, lon, radiusKm, sort, dir, openNowOnly);

        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * Detailed info for a Google doctor.
     *
     * Example:
     * GET /api/doctors/google/places/ChIJ.../detail
     */
    @GetMapping("/google/{placeId}/detail")
    public ResponseEntity<DoctorDetailsDTO> googleDetail(@PathVariable String placeId) {
        DoctorDetailsDTO dto = googleService.getDetails(placeId);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    private List<DoctorNearbyDTO> getLocalDoctors(String spec, double lat, double lon, double radius) {
        try {
            // Create criteria to search for doctors by specialization and location
            DoctorProfileCriteria criteria = new DoctorProfileCriteria();

            // Filter by specialization if provided
            if (spec != null && !spec.trim().isEmpty()) {
                StringFilter specializationFilter = new StringFilter();
                specializationFilter.setContains(spec);
                // Note: This is a simplified approach. In a real implementation, you'd need
                // to properly join with the specializations table
            }

            // Get all doctor profiles (we'll filter by distance in memory for simplicity)
            List<DoctorProfileDTO> doctorProfiles = doctorProfileQueryService.findByCriteria(criteria, Pageable.unpaged()).getContent();

            List<DoctorNearbyDTO> localDoctors = new ArrayList<>();

            for (DoctorProfileDTO profile : doctorProfiles) {
                if (profile.getLatitude() != null && profile.getLongitude() != null) {
                    double distance = calculateDistance(lat, lon, profile.getLatitude(), profile.getLongitude());

                    if (distance <= radius) {
                        DoctorNearbyDTO dto = new DoctorNearbyDTO();
                        dto.setProvider("LOCAL");
                        dto.setPlaceId(profile.getId()); // Use doctor profile ID as place ID
                        dto.setName(profile.getInternalUser() != null ? "Dr. " + profile.getInternalUser().getLogin() : "Dr. Unknown");
                        dto.setAddress(profile.getOfficeAddress());
                        dto.setLatitude(profile.getLatitude());
                        dto.setLongitude(profile.getLongitude());
                        dto.setDistanceKm(distance);
                        dto.setTravelDurationSec((int) (distance * 60 * 2)); // Rough estimate: 2 minutes per km
                        dto.setRating(4.5); // Default rating for local doctors
                        dto.setUserRatingCount(0); // No ratings system yet
                        dto.setOpenNow(true); // Assume local doctors are available
                        dto.setWeekdayDescriptions(List.of("Contact for availability"));

                        localDoctors.add(dto);
                    }
                }
            }

            return localDoctors;
        } catch (Exception e) {
            // Log error and return empty list
            System.err.println("Error fetching local doctors: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula to calculate distance between two points
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a =
            Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in km

        return distance;
    }

    private void sortDoctors(List<DoctorNearbyDTO> doctors, String sort, String dir) {
        Comparator<DoctorNearbyDTO> comparator;

        switch (sort.toLowerCase()) {
            case "rating":
                comparator = Comparator.comparing(DoctorNearbyDTO::getRating, Comparator.nullsLast(Double::compareTo));
                break;
            case "name":
                comparator = Comparator.comparing(DoctorNearbyDTO::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            default: // distance
                comparator = Comparator.comparing(DoctorNearbyDTO::getDistanceKm, Comparator.nullsLast(Double::compareTo));
                break;
        }

        if ("desc".equalsIgnoreCase(dir)) {
            comparator = comparator.reversed();
        }

        doctors.sort(comparator);
    }
}
