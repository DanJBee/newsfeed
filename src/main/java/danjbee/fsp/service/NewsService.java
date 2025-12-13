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
     * Retrieves news stories from the external API for a specific locale, category and page.
     * 
     * Results are cached to avoid hitting API rate limits and improve performance.
     * Cache is locale, category and page-specific so users can navigate pages and apply filters.
     * 
     * @param locale The country/region code (e.g., "us", "gb", "au"). Defaults to "us" if null.
     * @param category The news category (e.g., "general", "business", "technology"). Defaults to "general" if null.
     * @param page The page number (1-indexed). Defaults to 1 if less than 1.
     * @return List of NewsArticle objects containing article details
     */
    List<NewsArticle> getTopStories(String locale, String category, int page);
}