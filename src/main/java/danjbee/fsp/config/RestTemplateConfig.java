package danjbee.fsp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for REST client beans.
 * 
 * Provides a RestTemplate bean for making HTTP requests to external APIs.
 * This bean is used throughout the application for HTTP communication.
 * 
 * @author Daniel Bee
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates and configures a RestTemplate bean for HTTP requests.
     * 
     * RestTemplate is Spring's synchronous REST client used for making
     * HTTP requests to external services like The News API.
     * 
     * @return Configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
