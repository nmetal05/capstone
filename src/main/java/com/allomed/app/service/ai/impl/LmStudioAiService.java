package com.allomed.app.service.ai.impl;

import com.allomed.app.config.ApplicationProperties;
import com.allomed.app.service.ai.SymptomAiService;
import com.allomed.app.service.ai.dto.SuggestionResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Talks directly to LM Studio (OpenAI-compatible endpoint).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LmStudioAiService implements SymptomAiService {

    private final WebClient.Builder webClientBuilder;
    private final ApplicationProperties props;
    private final ObjectMapper mapper = new ObjectMapper();

    private String promptTemplate; // contains {MAX_SUGGESTIONS}
    private JsonNode schemaNode; // JSON-Schema for response_format

    /* Load prompt template & schema only once */
    @PostConstruct
    void init() throws Exception {
        promptTemplate = new String(
            new ClassPathResource("ai/system_prompt_template.txt").getInputStream().readAllBytes(),
            StandardCharsets.UTF_8
        );

        schemaNode = mapper.readTree(new ClassPathResource("ai/doctor_suggestions_schema.json").getInputStream());
    }

    @Override
    public SuggestionResponse inferSpecializations(String symptoms, Locale locale) {
        try {
            String systemPrompt = promptTemplate.replace("{MAX_SUGGESTIONS}", String.valueOf(props.getAi().getMaxSuggestions()));

            Map<String, Object> payload = Map.of(
                "model",
                "mistral-small-3.1-24b-instruct-2503",
                "messages",
                List.of(Map.of("role", "system", "content", systemPrompt), Map.of("role", "user", "content", symptoms)),
                "response_format",
                Map.of(
                    "type",
                    "json_schema",
                    "json_schema",
                    Map.of("name", "doctor_suggestions_response", "strict", true, "schema", schemaNode)
                ),
                "temperature",
                1,
                "max_tokens",
                1000,
                "stream",
                false
            );

            String raw = webClientBuilder
                .baseUrl(props.getAi().getUrl())
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(60));

            String content = mapper.readTree(raw).path("choices").get(0).path("message").path("content").asText();

            return mapper.readValue(content, SuggestionResponse.class);
        } catch (Exception ex) {
            log.error("LM Studio call failed", ex);
            return new SuggestionResponse(); // empty list
        }
    }
}
