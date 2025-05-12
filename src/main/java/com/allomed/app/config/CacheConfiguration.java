package com.allomed.app.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.redisson.Redisson;
// Import the RedissonClient interface
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;

    /**
     * Creates the RedissonClient bean using JHipster properties.
     * This bean manages the connection pool to Redis and can be injected
     * into other components like JCache or Bucket4j.
     * destroyMethod ensures Redisson shuts down cleanly with the application.
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(JHipsterProperties jHipsterProperties) {
        Config config = new Config();
        // Use the same codec as your original jcacheConfiguration
        config.setCodec(new org.redisson.codec.SerializationCodec());

        URI redisUri = URI.create(jHipsterProperties.getCache().getRedis().getServer()[0]);

        if (jHipsterProperties.getCache().getRedis().isCluster()) {
            ClusterServersConfig clusterServersConfig = config
                .useClusterServers()
                .setMasterConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setMasterConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .addNodeAddress(jHipsterProperties.getCache().getRedis().getServer());

            if (redisUri.getUserInfo() != null) {
                clusterServersConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        } else {
            SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .setAddress(jHipsterProperties.getCache().getRedis().getServer()[0]);

            if (redisUri.getUserInfo() != null) {
                singleServerConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        }
        // Create and return the RedissonClient instance
        return Redisson.create(config);
    }

    /**
     * Configures JCache (used by Hibernate 2nd level cache) to use the
     * RedissonClient bean created above.
     * Inject RedissonClient instead of creating a new instance here.
     */
    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration(
        JHipsterProperties jHipsterProperties,
        RedissonClient redissonClient // Inject the shared RedissonClient bean
    ) {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>();

        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(
            CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, jHipsterProperties.getCache().getRedis().getExpiration()))
        );

        // Use the injected redissonClient bean
        return RedissonConfiguration.fromInstance(redissonClient, jcacheConfig);
    }

    // --- NO CHANGES NEEDED BELOW THIS LINE ---
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cm) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cm);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cm -> {
            createCache(cm, com.allomed.app.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            createCache(cm, com.allomed.app.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.User.class.getName(), jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.Authority.class.getName(), jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.AppUserProfile.class.getName(), jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.DoctorProfile.class.getName(), jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.DoctorProfile.class.getName() + ".specializations", jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.Specialization.class.getName(), jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.Specialization.class.getName() + ".doctorProfiles", jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.SymptomSearch.class.getName(), jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.SymptomSearchRecommendation.class.getName(), jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.DoctorDocument.class.getName(), jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.GuestSession.class.getName(), jcacheConfiguration);
            createCache(cm, com.allomed.app.domain.DoctorViewHistory.class.getName(), jcacheConfiguration);
            // jhipster-needle-redis-add-entry
        };
    }

    private void createCache(
        javax.cache.CacheManager cm,
        String cacheName,
        javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration
    ) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear(); // Keep your original behaviour
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
    // --- End of unchanged section ---
}
