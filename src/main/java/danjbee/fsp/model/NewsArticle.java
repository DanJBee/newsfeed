package danjbee.fsp.model;

import lombok.Data;

/**
 * Data model representing a news article.
 * 
 * Using Lombok's @Data annotation to automatically generate:
 * - Getters for all fields
 * - Setters for all fields
 * - toString() method
 * - equals() and hashCode() methods
 * - Required args constructor
 * 
 * This reduces boilerplate code and improves maintainability.
 * 
 * @author Daniel Bee
 */
@Data
public class NewsArticle {
    
    /** The headline/title of the article */
    private String title;
    
    /** Brief description or excerpt from the article */
    private String description;
    
    /** Full URL to the original article */
    private String url;
    
    /** URL to the article's thumbnail/featured image */
    private String imageUrl;
    
    /** Publication date/time of the article */
    private String publishedAt;
    
    /** Name of the news source/publisher */
    private String source;
}
