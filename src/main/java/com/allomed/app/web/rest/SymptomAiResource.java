package com.allomed.app.web.rest;

import com.allomed.app.service.ai.SymptomAiService;
import com.allomed.app.service.ai.dto.SuggestionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "LLM-powered doctor specialisation helper")
@RequiredArgsConstructor
public class SymptomAiResource {

    private final SymptomAiService aiService;

    @Operation(summary = "Infer doctor specialisations from patient symptoms")
    @PostMapping("/symptom-to-spec")
    public ResponseEntity<SuggestionResponse> infer(@RequestBody SymptomRequest body, Locale locale) {
        SuggestionResponse response = aiService.inferSpecializations(body.symptoms(), locale);
        return ResponseEntity.ok(response);
    }

    /* -------- Request DTO (record) -------- */
    public record SymptomRequest(@NotBlank String symptoms) {}
}
