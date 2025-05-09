package com.allomed.app.service.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/** One item in the LLM response array. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Suggestion {

    private String specialization; // e.g. "Cardiologist"
    private double confidence; // 0–1
    private String reason; // short explanation
}
