package danjbee.fsp.model;

import lombok.Data;

/**
 * Data model for a news article.
 * Using Lombok @Data to auto-generate getters/setters.
 * 
 * @author Daniel Bee
 */
@Data
public class NewsArticle {
    
    private String title;
    private String description;
    private String url;
    private String imageUrl;
    private String publishedAt;
    private String source;
}
