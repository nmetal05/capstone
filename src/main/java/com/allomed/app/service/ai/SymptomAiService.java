package com.allomed.app.service.ai;

import com.allomed.app.service.ai.dto.SuggestionResponse;
import java.util.Locale;

/**
 * Contract for calling the LLM and getting doctor-specialisation suggestions.
 */
public interface SymptomAiService {
    /**
     * @param symptoms  free-text entered by user
     * @param locale    UI locale, in case you want multi-lingual prompts later
     * @return          array of specialisations with confidence + reason
     */
    SuggestionResponse inferSpecializations(String symptoms, Locale locale);
}
