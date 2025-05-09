package com.allomed.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Allomed.
 * Properties are configured in the {@code application.yml} file
 * under the "application.*" namespace.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final Ai ai = new Ai(); // <── NEW

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public Ai getAi() { // <── NEW
        return ai;
    }

    // jhipster-needle-application-properties-property-getter

    /* ---------------- Inner classes ---------------- */

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    // NEW inner class that holds the LM-Studio settings
    public static class Ai {

        /** LM Studio/OpenAI-compatible URL */
        private String url;
        /** Max number of specialisations LLM may return */
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

        public void setMaxSuggestions(int max) {
            this.maxSuggestions = max;
        }
    }
    // jhipster-needle-application-properties-property-class
}
