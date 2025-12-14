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
 * Service implementation that fetches news from The News API.
 * 
 * @author Daniel Bee
 */
@Service
@PropertySource("classpath:token.properties")
public class NewsServiceImpl implements NewsService {

    @Value("${token}")
    private String token;
    
    private final RestTemplate restTemplate;
    
    public NewsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Fetches news stories - results are cached to avoid hitting API rate limits
    @Override
    @Cacheable(value = "newsCache", key = "#locale + '-' + #category + '-' + #page", unless = "#result.isEmpty()")
    public List<NewsArticle> getTopStories(String locale, String category, int page) {
        if (locale == null || locale.trim().isEmpty()) {
            locale = "us";
        }
        
        if (category == null || category.trim().isEmpty()) {
            category = "general";
        }
        
        if (page < 1) {
            page = 1;
        }
        
        int limit = 3;
        
        // Build API URL
        String apiUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + token 
                + "&locale=" + locale + "&categories=" + category 
                + "&limit=" + limit + "&page=" + page;
        
        List<NewsArticle> articles = new ArrayList<>();
        
        try {
            String response = restTemplate.getForObject(apiUrl, String.class);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode dataNode = root.get("data");
            
            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode articleNode : dataNode) {
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
            // Return empty list if API call fails
            System.err.println("Error fetching news: " + e.getMessage());
        }
        
        return articles;
    }
}