package com.allomed.app.service.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collections;
import java.util.List;
import lombok.*;

/** Root object returned by the LLM. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuggestionResponse {

    private List<Suggestion> suggestions = Collections.emptyList();
}
