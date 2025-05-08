package com.allomed.app.web.rest;

import com.allomed.app.domain.AppUserProfile;
import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.domain.User;
import com.allomed.app.repository.AppUserProfileRepository;
import com.allomed.app.repository.DoctorProfileRepository;
import com.allomed.app.repository.UserRepository;
import com.allomed.app.security.SecurityUtils;
import com.allomed.app.service.KeycloakAdminService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profile")
public class ProfileTypeResource {

    private final Logger log = LoggerFactory.getLogger(ProfileTypeResource.class);

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppUserProfileRepository appUserProfileRepository;
    private final KeycloakAdminService keycloakAdminService;

    public ProfileTypeResource(
        UserRepository userRepository,
        DoctorProfileRepository doctorProfileRepository,
        AppUserProfileRepository appUserProfileRepository,
        KeycloakAdminService keycloakAdminService
    ) {
        this.userRepository = userRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.appUserProfileRepository = appUserProfileRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

    /* ────────────────────────────────────────────────────────────── */
    public static class ChooseTypeDTO {

        public String type;
        public String inpeCode;
        public Double latitude;
        public Double longitude;
        public String officeAddress;
        public String phoneNumber;
    }

    public static class ChooseTypeResult {

        public String message;

        public ChooseTypeResult(String message) {
            this.message = message;
        }
    }

    public static class ProfileStatusDTO {

        public List<String> authorities;
        public boolean hasAppUserProfile;
        public boolean hasDoctorProfile;
        public boolean doctorProfileComplete;

        public ProfileStatusDTO(
            List<String> authorities,
            boolean hasAppUserProfile,
            boolean hasDoctorProfile,
            boolean doctorProfileComplete
        ) {
            this.authorities = authorities;
            this.hasAppUserProfile = hasAppUserProfile;
            this.hasDoctorProfile = hasDoctorProfile;
            this.doctorProfileComplete = doctorProfileComplete;
        }
    }

    /* ───────────────────────── POST /choose-type ───────────────────────── */
    @PostMapping("/choose-type")
    @Transactional
    public ResponseEntity<ChooseTypeResult> chooseType(@RequestBody ChooseTypeDTO body) {
        String login = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user not found"));

        User user = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isAdmin = user.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getName()));
        if (isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ChooseTypeResult("Admins cannot be assigned as App User or Doctor")
            );
        }

        /* -------- doctor -------- */
        if ("DOCTOR".equalsIgnoreCase(body.type)) {
            if (
                body.inpeCode == null ||
                body.latitude == null ||
                body.longitude == null ||
                body.officeAddress == null ||
                body.phoneNumber == null
            ) {
                return ResponseEntity.badRequest().body(new ChooseTypeResult("Missing required doctor fields."));
            }

            keycloakAdminService.addRoleToUser(login, "ROLE_DOCTOR");

            DoctorProfile profile = doctorProfileRepository.findOneByInternalUser_Id(user.getId()).orElseGet(DoctorProfile::new);

            boolean isNew = profile.getId() == null;

            profile.setInternalUser(user);
            profile.setInpeCode(body.inpeCode);
            profile.setLatitude(body.latitude);
            profile.setLongitude(body.longitude);
            profile.setOfficeAddress(body.officeAddress);
            profile.setPhoneNumber(body.phoneNumber);

            /* --- critical line: ensure @NotNull field has a value ---- */
            if (isNew) {
                profile.setIsVerified(false); // default – real verification later
            }

            doctorProfileRepository.save(profile);

            return ResponseEntity.ok(new ChooseTypeResult("Doctor profile saved and role assigned."));
            /* -------- app user -------- */
        } else if ("APP_USER".equalsIgnoreCase(body.type)) {
            keycloakAdminService.addRoleToUser(login, "ROLE_APP_USER");

            if (!appUserProfileRepository.existsByInternalUser_Id(user.getId())) {
                AppUserProfile profile = new AppUserProfile();
                profile.setInternalUser(user);
                appUserProfileRepository.save(profile);
            }
            return ResponseEntity.ok(new ChooseTypeResult("App user profile created and role assigned."));
            /* -------- invalid -------- */
        } else {
            return ResponseEntity.badRequest().body(new ChooseTypeResult("Invalid type: must be 'DOCTOR' or 'APP_USER'."));
        }
    }

    /* ───────────────────────── GET /status ───────────────────────── */
    @GetMapping("/status")
    @Transactional(readOnly = true)
    public ResponseEntity<ProfileStatusDTO> getProfileStatus() {
        try {
            String login = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user not found"));

            User user = userRepository
                .findOneByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            List<String> authorities = user.getAuthorities().stream().map(a -> a.getName()).collect(Collectors.toList());

            boolean hasAppUser = appUserProfileRepository.existsByInternalUser_Id(user.getId());
            Optional<DoctorProfile> dpOpt = doctorProfileRepository.findOneByInternalUser_Id(user.getId());
            boolean hasDoctor = dpOpt.isPresent();

            boolean doctorComplete = false;
            if (dpOpt.isPresent()) {
                DoctorProfile dp = dpOpt.get();
                doctorComplete =
                    dp.getInpeCode() != null &&
                    dp.getLatitude() != null &&
                    dp.getLongitude() != null &&
                    dp.getOfficeAddress() != null &&
                    dp.getPhoneNumber() != null;
            }

            return ResponseEntity.ok(new ProfileStatusDTO(authorities, hasAppUser, hasDoctor, doctorComplete));
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            log.error("Error retrieving profile status", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving profile status");
        }
    }
}
