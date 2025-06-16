package com.allomed.app.web.rest;

import com.allomed.app.domain.AppUserProfile;
import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.domain.DoctorViewHistory;
import com.allomed.app.domain.SymptomSearch;
import com.allomed.app.domain.SymptomSearchRecommendation;
import com.allomed.app.domain.User;
import com.allomed.app.repository.AppUserProfileRepository;
import com.allomed.app.repository.DoctorProfileRepository;
import com.allomed.app.repository.DoctorViewHistoryRepository;
import com.allomed.app.repository.SymptomSearchRecommendationRepository;
import com.allomed.app.repository.SymptomSearchRepository;
import com.allomed.app.repository.UserRepository;
import com.allomed.app.security.SecurityUtils;
import com.allomed.app.service.AppUserProfileService;
import com.allomed.app.service.dto.AppUserProfileDTO;
import com.allomed.app.service.dto.DoctorViewHistoryDTO;
import com.allomed.app.service.dto.SymptomSearchDTO;
import com.allomed.app.service.dto.SymptomSearchRecommendationDTO;
import com.allomed.app.service.mapper.DoctorViewHistoryMapper;
import com.allomed.app.service.mapper.SymptomSearchMapper;
import com.allomed.app.service.mapper.SymptomSearchRecommendationMapper;
import com.allomed.app.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing user history (symptom searches and doctor views).
 */
@RestController
@RequestMapping("/api/user-history")
public class UserHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserHistoryResource.class);

    private final SymptomSearchRepository symptomSearchRepository;
    private final DoctorViewHistoryRepository doctorViewHistoryRepository;
    private final SymptomSearchRecommendationRepository symptomSearchRecommendationRepository;
    private final SymptomSearchMapper symptomSearchMapper;
    private final DoctorViewHistoryMapper doctorViewHistoryMapper;
    private final SymptomSearchRecommendationMapper symptomSearchRecommendationMapper;
    private final AppUserProfileService appUserProfileService;
    private final AppUserProfileRepository appUserProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;

    public UserHistoryResource(
        SymptomSearchRepository symptomSearchRepository,
        DoctorViewHistoryRepository doctorViewHistoryRepository,
        SymptomSearchRecommendationRepository symptomSearchRecommendationRepository,
        SymptomSearchMapper symptomSearchMapper,
        DoctorViewHistoryMapper doctorViewHistoryMapper,
        SymptomSearchRecommendationMapper symptomSearchRecommendationMapper,
        AppUserProfileService appUserProfileService,
        AppUserProfileRepository appUserProfileRepository,
        DoctorProfileRepository doctorProfileRepository,
        UserRepository userRepository
    ) {
        this.symptomSearchRepository = symptomSearchRepository;
        this.doctorViewHistoryRepository = doctorViewHistoryRepository;
        this.symptomSearchRecommendationRepository = symptomSearchRecommendationRepository;
        this.symptomSearchMapper = symptomSearchMapper;
        this.doctorViewHistoryMapper = doctorViewHistoryMapper;
        this.symptomSearchRecommendationMapper = symptomSearchRecommendationMapper;
        this.appUserProfileService = appUserProfileService;
        this.appUserProfileRepository = appUserProfileRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/user-history/symptom-searches : get current user's symptom search history.
     *
     * @param pageable the pagination information.
     * @return the ResponseEntity with status 200 (OK) and the list of symptom searches in body.
     */
    @GetMapping("/symptom-searches")
    public ResponseEntity<List<SymptomSearchDTO>> getCurrentUserSymptomSearches(Pageable pageable) {
        LOG.debug("REST request to get current user's symptom search history");

        try {
            // Get current user's profile
            String currentUserLogin = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestAlertException("Current user login not found", "userHistory", "nologin"));

            // Find the current user first
            Optional<User> currentUser = userRepository.findOneByLogin(currentUserLogin);
            if (currentUser.isEmpty()) {
                throw new BadRequestAlertException("Current user not found", "userHistory", "usernotfound");
            }

            // Find the user's profile using the user ID
            Optional<AppUserProfile> currentUserProfileOpt = appUserProfileRepository.findById(currentUser.get().getId());
            if (currentUserProfileOpt.isEmpty()) {
                throw new BadRequestAlertException("User profile not found", "userHistory", "profilenotfound");
            }

            AppUserProfile currentUserProfile = currentUserProfileOpt.get();

            LOG.debug("Loading symptom searches for user profile ID: {}", currentUserProfile.getId());
            Page<SymptomSearch> page = symptomSearchRepository.findByUserIdWithEagerRelationships(currentUserProfile.getId(), pageable);

            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

            LOG.debug("Found {} symptom searches for user", page.getTotalElements());
            return ResponseEntity.ok().headers(headers).body(page.map(symptomSearchMapper::toDto).getContent());
        } catch (Exception e) {
            LOG.error(
                "Exception in getCurrentUserSymptomSearches() with cause = '{}' and exception = '{}'",
                e.getCause() != null ? e.getCause().toString() : "null",
                e.getMessage(),
                e
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/user-history/doctor-views : get current user's doctor view history.
     *
     * @param pageable the pagination information.
     * @return the ResponseEntity with status 200 (OK) and the list of doctor views in body.
     */
    @GetMapping("/doctor-views")
    public ResponseEntity<List<DoctorViewHistoryDTO>> getCurrentUserDoctorViews(Pageable pageable) {
        LOG.debug("REST request to get current user's doctor view history");

        try {
            // Get current user's profile
            String currentUserLogin = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestAlertException("Current user login not found", "userHistory", "nologin"));

            // Find the current user first
            Optional<User> currentUser = userRepository.findOneByLogin(currentUserLogin);
            if (currentUser.isEmpty()) {
                throw new BadRequestAlertException("Current user not found", "userHistory", "usernotfound");
            }

            // Find the user's profile using the user ID
            Optional<AppUserProfile> currentUserProfileOpt = appUserProfileRepository.findById(currentUser.get().getId());
            if (currentUserProfileOpt.isEmpty()) {
                throw new BadRequestAlertException("User profile not found", "userHistory", "profilenotfound");
            }

            AppUserProfile currentUserProfile = currentUserProfileOpt.get();

            LOG.debug("Loading doctor views for user profile ID: {}", currentUserProfile.getId());
            Page<DoctorViewHistory> page = doctorViewHistoryRepository.findByUserIdWithEagerRelationships(
                currentUserProfile.getId(),
                pageable
            );

            // Load specializations separately to avoid pagination issues
            List<DoctorViewHistory> doctorViewsWithSpecializations;
            if (!page.getContent().isEmpty()) {
                List<Long> ids = page.getContent().stream().map(DoctorViewHistory::getId).toList();
                doctorViewsWithSpecializations = doctorViewHistoryRepository.findByIdsWithSpecializations(ids);
            } else {
                doctorViewsWithSpecializations = page.getContent();
            }

            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

            LOG.debug("Found {} doctor views for user", page.getTotalElements());
            return ResponseEntity.ok()
                .headers(headers)
                .body(doctorViewsWithSpecializations.stream().map(doctorViewHistoryMapper::toDto).toList());
        } catch (Exception e) {
            LOG.error(
                "Exception in getCurrentUserDoctorViews() with cause = '{}' and exception = '{}'",
                e.getCause() != null ? e.getCause().toString() : "null",
                e.getMessage(),
                e
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/user-history/symptom-search-recommendations/{searchId} : get recommendations for a specific symptom search.
     *
     * @param searchId the ID of the symptom search
     * @return the ResponseEntity with status 200 (OK) and the list of recommendations in body
     */
    @GetMapping("/symptom-search-recommendations/{searchId}")
    public ResponseEntity<List<SymptomSearchRecommendationDTO>> getSymptomSearchRecommendations(@PathVariable Long searchId) {
        LOG.debug("REST request to get recommendations for symptom search: {}", searchId);

        try {
            // Verify the search belongs to the current user
            String currentUserLogin = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestAlertException("Current user login not found", "userHistory", "nologin"));

            // Find the current user first
            Optional<User> currentUser = userRepository.findOneByLogin(currentUserLogin);
            if (currentUser.isEmpty()) {
                throw new BadRequestAlertException("Current user not found", "userHistory", "usernotfound");
            }

            // Find the user's profile using the user ID
            Optional<AppUserProfile> currentUserProfileOpt = appUserProfileRepository.findById(currentUser.get().getId());
            if (currentUserProfileOpt.isEmpty()) {
                throw new BadRequestAlertException("User profile not found", "userHistory", "profilenotfound");
            }

            AppUserProfile currentUserProfile = currentUserProfileOpt.get();

            Optional<SymptomSearch> symptomSearch = symptomSearchRepository.findById(searchId);
            if (symptomSearch.isEmpty()) {
                LOG.debug("Symptom search not found: {}", searchId);
                return ResponseEntity.notFound().build();
            }

            if (!symptomSearch.get().getUser().getId().equals(currentUserProfile.getId())) {
                LOG.debug("Symptom search {} does not belong to current user", searchId);
                return ResponseEntity.notFound().build();
            }

            List<SymptomSearchRecommendationDTO> recommendations = symptomSearchRecommendationRepository
                .findBySearchIdWithEagerRelationships(searchId)
                .stream()
                .map(symptomSearchRecommendationMapper::toDto)
                .toList();

            LOG.debug("Found {} recommendations for symptom search {}", recommendations.size(), searchId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            LOG.error(
                "Exception in getSymptomSearchRecommendations() with cause = '{}' and exception = '{}'",
                e.getCause() != null ? e.getCause().toString() : "null",
                e.getMessage(),
                e
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/user-history/google-doctor-views : save a Google Maps doctor view to history.
     *
     * @param googleDoctorData the Google Maps doctor data to save
     * @return the ResponseEntity with status 201 (Created) or appropriate error status
     */
    @PostMapping("/google-doctor-views")
    public ResponseEntity<Void> saveGoogleDoctorView(@RequestBody GoogleDoctorViewRequest googleDoctorData) {
        LOG.debug("REST request to save Google Maps doctor view: {}", googleDoctorData);
        try {
            // Get current user's profile
            String currentUserLogin = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestAlertException("Current user login not found", "userHistory", "nologin"));

            // Find the current user first
            Optional<User> currentUser = userRepository.findOneByLogin(currentUserLogin);
            if (currentUser.isEmpty()) {
                throw new BadRequestAlertException("Current user not found", "userHistory", "usernotfound");
            }

            // Find the user's profile using the user ID
            Optional<AppUserProfile> currentUserProfileOpt = appUserProfileRepository.findById(currentUser.get().getId());
            if (currentUserProfileOpt.isEmpty()) {
                throw new BadRequestAlertException("User profile not found", "userHistory", "profilenotfound");
            }

            AppUserProfile currentUserProfile = currentUserProfileOpt.get();

            // Check if this exact view already exists to avoid duplicates
            Pageable recentViewsPageable = PageRequest.of(0, 10); // Get last 10 views
            Page<DoctorViewHistory> recentViewsPage = doctorViewHistoryRepository.findByUserIdOrderByViewDateDesc(
                currentUserProfile.getId(),
                recentViewsPageable
            );
            List<DoctorViewHistory> existingViews = recentViewsPage.getContent();

            boolean viewAlreadyExists = existingViews
                .stream()
                .anyMatch(
                    view ->
                        view.getGoogleDoctorData() != null &&
                        view.getGoogleDoctorData().contains(googleDoctorData.getPlaceId()) &&
                        view.getViewDate().isAfter(Instant.now().minusSeconds(60))
                ); // Within last minute

            if (!viewAlreadyExists) {
                // Create the view history entry with Google Maps doctor data as JSON
                DoctorViewHistory viewHistory = new DoctorViewHistory();
                viewHistory.setViewDate(Instant.now());
                viewHistory.setUser(currentUserProfile);
                viewHistory.setDoctor(null); // No DoctorProfile for Google Maps doctors

                // Store Google Maps doctor data as JSON
                String googleDoctorJson = String.format(
                    "{\"placeId\":\"%s\",\"doctorName\":\"%s\",\"address\":\"%s\",\"latitude\":%f,\"longitude\":%f,\"rating\":%f,\"userRatingCount\":%d,\"openNow\":%b,\"phoneNumber\":\"%s\",\"website\":\"%s\",\"weekdayDescriptions\":%s}",
                    googleDoctorData.getPlaceId(),
                    googleDoctorData.getDoctorName().replace("\"", "\\\""), // Escape quotes
                    googleDoctorData.getAddress().replace("\"", "\\\""), // Escape quotes
                    googleDoctorData.getLatitude(),
                    googleDoctorData.getLongitude(),
                    googleDoctorData.getRating() != null ? googleDoctorData.getRating() : 0.0,
                    googleDoctorData.getUserRatingCount() != null ? googleDoctorData.getUserRatingCount() : 0,
                    googleDoctorData.getOpenNow() != null ? googleDoctorData.getOpenNow() : false,
                    googleDoctorData.getPhoneNumber() != null ? googleDoctorData.getPhoneNumber().replace("\"", "\\\"") : "",
                    googleDoctorData.getWebsite() != null ? googleDoctorData.getWebsite().replace("\"", "\\\"") : "",
                    googleDoctorData.getWeekdayDescriptions() != null ? googleDoctorData.getWeekdayDescriptions() : "[]"
                );

                viewHistory.setGoogleDoctorData(googleDoctorJson);

                doctorViewHistoryRepository.save(viewHistory);
                LOG.debug("Created new view history entry for Google Maps doctor: {}", googleDoctorData.getPlaceId());
            } else {
                LOG.debug("View history entry already exists for Google Maps doctor: {}", googleDoctorData.getPlaceId());
            }

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            LOG.error("Error saving Google Maps doctor view: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DTO for Google Maps doctor view requests
     */
    public static class GoogleDoctorViewRequest {

        private String placeId;
        private String doctorName;
        private String address;
        private Double latitude;
        private Double longitude;
        private Double rating;
        private Integer userRatingCount;
        private Boolean openNow;
        private String phoneNumber;
        private String website;
        private String weekdayDescriptions;

        // Getters and setters
        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public void setDoctorName(String doctorName) {
            this.doctorName = doctorName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }

        public Integer getUserRatingCount() {
            return userRatingCount;
        }

        public void setUserRatingCount(Integer userRatingCount) {
            this.userRatingCount = userRatingCount;
        }

        public Boolean getOpenNow() {
            return openNow;
        }

        public void setOpenNow(Boolean openNow) {
            this.openNow = openNow;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getWeekdayDescriptions() {
            return weekdayDescriptions;
        }

        public void setWeekdayDescriptions(String weekdayDescriptions) {
            this.weekdayDescriptions = weekdayDescriptions;
        }
    }
}
