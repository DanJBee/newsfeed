package danjbee.fsp.service;

import danjbee.fsp.model.NewsArticle;
import java.util.List;

/**
 * Service interface for fetching news articles.
 * 
 * This interface follows the Dependency Inversion Principle (SOLID) by defining
 * a contract that decouples the controller from the concrete implementation.
 * This makes the code more testable and maintainable.
 * 
 * @author Daniel Bee
 */
public interface NewsService {
    
    /**
     * Retrieves the top news stories from the external API for a specific locale.
     * 
     * Results are cached to avoid hitting API rate limits and improve performance.
     * Cache is locale-specific so users can switch between regions without stale data.
     * 
     * @param locale The country/region code (e.g., "us", "gb", "au"). Defaults to "us" if null.
     * @return List of NewsArticle objects containing article details
     */
    List<NewsArticle> getTopStories(String locale);
}