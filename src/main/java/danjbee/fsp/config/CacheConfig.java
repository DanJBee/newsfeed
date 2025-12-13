package danjbee.fsp.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for application caching.
 * 
 * Sets up Caffeine as the cache provider for Spring's caching abstraction.
 * Caffeine is a high-performance, near-optimal caching library for Java 8+.
 * 
 * Why caching is important here:
 * - The News API has strict rate limits
 * - News doesn't change every second, so caching is acceptable
 * - Improves response time for users
 * - Reduces external API calls and associated costs
 * 
 * @author Daniel Bee
 */
@Configuration
public class CacheConfig {

    /**
     * Creates and configures the CacheManager bean.
     * 
     * Cache configuration:
     * - Maximum 100 entries to prevent excessive memory usage
     * - Entries expire after 24 hours to ensure data freshness
     * - Cache name "newsCache" matches the @Cacheable annotation in the service
     * 
     * @return Configured CacheManager for the application
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("newsCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100) // Limit cache size to prevent memory issues
                .expireAfterWrite(24, TimeUnit.HOURS)); // Auto-expire after 24 hours
        return cacheManager;
    }
}
