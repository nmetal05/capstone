package com.allomed.app.web.rest;

// Local imports
import com.allomed.app.aop.ratelimit.RateLimited; // <<< ADD Import for RateLimited
import com.allomed.app.security.AuthoritiesConstants; // <<< ADD Import for security constants
import com.allomed.app.service.ai.SymptomAiService;
import com.allomed.app.service.ai.dto.SuggestionResponse;
// Third-party imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <<< ADD Import for PreAuthorize
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "LLM-powered doctor specialisation helper")
@RequiredArgsConstructor
public class SymptomAiResource {

    private final SymptomAiService aiService;

    @Operation(summary = "Infer doctor specialisations from patient symptoms")
    @PostMapping("/symptom-to-spec")
    // --- V V V --- ANNOTATIONS ADDED HERE --- V V V ---
    @PreAuthorize("!hasAuthority('" + AuthoritiesConstants.DOCTOR + "')") // Deny if user has DOCTOR role
    @RateLimited // Apply role-based rate limiting (Admin=unlimited, User=250/day, Guest=5/day)
    // --- ^ ^ ^ --- ANNOTATIONS ADDED HERE --- ^ ^ ^ ---
    public ResponseEntity<SuggestionResponse> infer(@RequestBody SymptomRequest body, Locale locale) {
        SuggestionResponse response = aiService.inferSpecializations(body.symptoms(), locale);
        return ResponseEntity.ok(response);
    }

    /* -------- Request DTO (record) -------- */
    public record SymptomRequest(@NotBlank String symptoms) {}
}
