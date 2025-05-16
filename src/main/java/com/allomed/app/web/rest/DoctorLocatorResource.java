package com.allomed.app.web.rest;

import com.allomed.app.service.dto.DoctorDetailsDTO;
import com.allomed.app.service.dto.DoctorNearbyDTO;
import com.allomed.app.service.google.GooglePlacesService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorLocatorResource {

    private final GooglePlacesService googleService;

    /**
     * List nearby doctors by specialization, radius & optional sorting.
     *
     * Example:
     * GET /api/doctors/nearby?spec=cardiology&lat=33.51&lon=-7.64&radiusKm=3
     */
    @GetMapping("/nearby")
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
}
