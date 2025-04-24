package com.allomed.app;

import com.allomed.app.config.AsyncSyncConfiguration;
import com.allomed.app.config.EmbeddedElasticsearch;
import com.allomed.app.config.EmbeddedRedis;
import com.allomed.app.config.EmbeddedSQL;
import com.allomed.app.config.JacksonConfiguration;
import com.allomed.app.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { AllomedApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class })
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
public @interface IntegrationTest {
}
