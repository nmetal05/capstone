package com.allomed.app.config;

// Bucket4j Imports
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.redisson.cas.RedissonBasedProxyManager;
// <<< ADD IMPORT FOR CAST >>>
import org.redisson.Redisson;
// Redisson Imports
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
// Spring Imports
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Bucket4j to use Redis via Redisson.
 */
@Configuration
public class RateLimitingConfiguration {

    /**
     * Creates the Bucket4j ProxyManager bean needed for Redis interaction.
     */
    @Bean
    public ProxyManager<String> bucket4jProxyManager(RedissonClient redissonClient) {
        // <<< FIX START >>>
        // Cast the injected RedissonClient to the concrete Redisson class
        // to access the getCommandExecutor() method if it's not on the interface.
        if (!(redissonClient instanceof org.redisson.Redisson)) {
            // Add a check for safety, although Redisson.create() should return this type.
            throw new IllegalStateException(
                "Injected RedissonClient is not an instance of org.redisson.Redisson. Cannot get CommandAsyncExecutor."
            );
        }
        CommandAsyncExecutor commandExecutor = ((org.redisson.Redisson) redissonClient).getCommandExecutor();
        // <<< FIX END >>>

        // Build and return the ProxyManager configured for Redisson
        return RedissonBasedProxyManager.builderFor(commandExecutor).build();
    }
}
