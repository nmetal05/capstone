package com.allomed.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Allomed.
 *
 * They are configured in the {@code application*.yml} files
 * (or .env, thanks to spring-dotenv) under the "application.*" namespace.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final Ai ai = new Ai();
    private final ExternalPlaces externalPlaces = new ExternalPlaces();

    // ------------------------------------------------------------------
    // Getters for top-level groups
    // ------------------------------------------------------------------
    public Liquibase getLiquibase() {
        return liquibase;
    }

    public Ai getAi() {
        return ai;
    }

    public ExternalPlaces getExternalPlaces() {
        return externalPlaces;
    }

    // ------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------

    /**
     * Liquibase-specific tuning options.
     */
    public static class Liquibase {

        /** Whether to run Liquibase asynchronously on startup. */
        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    /**
     * Settings for the local LLM (LM-Studio) that analyses symptoms.
     */
    public static class Ai {

        /** LM-Studio / OpenAI-compatible base URL. */
        private String url;
        /** Max number of specialisations the LLM may return. */
        private int maxSuggestions = 3;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getMaxSuggestions() {
            return maxSuggestions;
        }

        public void setMaxSuggestions(int maxSuggestions) {
            this.maxSuggestions = maxSuggestions;
        }
    }

    /**
     * Configuration for external place-provider (Google Places).
     */
    public static class ExternalPlaces {

        /** Which provider is active. Default = GOOGLE. */
        private String provider = "GOOGLE";
        /** API key or token for the chosen provider. */
        private String apiKey;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }
}
