package danjbee.fsp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import danjbee.fsp.model.NewsArticle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of NewsService that fetches news from The News API.
 * 
 * This class demonstrates several important design patterns and practices:
 * - Service Layer Pattern: Separates business logic from controller logic
 * - Dependency Injection: Spring automatically manages the lifecycle
 * - Caching: Results are cached for 24 hours to respect API rate limits
 * - Error Handling: Gracefully handles API failures by returning empty list
 * 
 * @author Daniel Bee
 */
@Service
@PropertySource("classpath:token.properties") // Loads API token from external properties file
public class NewsServiceImpl implements NewsService {

    // Inject API token from properties file - keeps sensitive data out of code
    @Value("${token}")
    private String token;

    /**
     * Fetches top news stories from The News API for a specific locale and category.
     * 
     * Uses Spring's caching abstraction with Caffeine to cache results for 24 hours.
     * This prevents excessive API calls and improves response time for users.
     * Cache key combines locale and category to ensure filter-specific caching.
     * 
     * @param locale The country/region code (e.g., "us", "gb", "au")
     * @param category The news category (e.g., "general", "business", "technology")
     * @return List of up to 3 news articles, or empty list if API call fails
     */
    @Override
    @Cacheable(value = "newsCache", key = "#locale + '-' + #category", unless = "#result.isEmpty()")
    public List<NewsArticle> getTopStories(String locale, String category) {
        // Default to US if no locale specified
        if (locale == null || locale.trim().isEmpty()) {
            locale = "us";
        }
        
        // Default to general if no category specified
        if (category == null || category.trim().isEmpty()) {
            category = "general";
        }
        
        // Using RestTemplate for HTTP communication - Spring's synchronous REST client
        RestTemplate restTemplate = new RestTemplate();
        // Build API URL with dynamic locale and category parameters
        String apiUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + token 
                + "&locale=" + locale + "&categories=" + category + "&limit=3";
        
        List<NewsArticle> articles = new ArrayList<>();
        
        try {
            // Make GET request to external API
            String response = restTemplate.getForObject(apiUrl, String.class);
            
            // Parse JSON response using Jackson ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode dataNode = root.get("data"); // Articles are in the 'data' array
            
            // Extract article data if present
            if (dataNode != null && dataNode.isArray()) {
                // Iterate through each article in the response
                for (JsonNode articleNode : dataNode) {
                    // Map JSON fields to NewsArticle object
                    NewsArticle article = new NewsArticle();
                    article.setTitle(articleNode.path("title").asText(""));
                    article.setDescription(articleNode.path("description").asText(""));
                    article.setUrl(articleNode.path("url").asText(""));
                    article.setImageUrl(articleNode.path("image_url").asText(""));
                    article.setPublishedAt(articleNode.path("published_at").asText(""));
                    article.setSource(articleNode.path("source").asText(""));
                    articles.add(article);
                }
            }
        } catch (Exception e) {
            // Log error but don't crash - return empty list instead
            // In production, would use proper logging framework like SLF4J
            System.err.println("Error fetching news: " + e.getMessage());
        }
        
        return articles;
    }
}