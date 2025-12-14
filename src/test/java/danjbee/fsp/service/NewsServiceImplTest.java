package danjbee.fsp.service;

import danjbee.fsp.model.NewsArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NewsServiceImpl.
 * 
 * @author Daniel Bee
 */
@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    private NewsServiceImpl newsService;

    private final String testToken = "test-api-token";
    
    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(restTemplate);
        ReflectionTestUtils.setField(newsService, "token", testToken);
    }

    @Test
    void testGetTopStories_Success() {
        String locale = "us";
        String category = "business";
        int page = 1;
        
        String mockJsonResponse = """
                {
                    "data": [
                        {
                            "title": "Test Article 1",
                            "description": "Description 1",
                            "url": "https://example.com/article1",
                            "image_url": "https://example.com/image1.jpg",
                            "published_at": "2025-12-13T10:00:00Z",
                            "source": "Test Source 1"
                        },
                        {
                            "title": "Test Article 2",
                            "description": "Description 2",
                            "url": "https://example.com/article2",
                            "image_url": "https://example.com/image2.jpg",
                            "published_at": "2025-12-13T11:00:00Z",
                            "source": "Test Source 2"
                        }
                    ]
                }
                """;
        
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=" + locale + "&categories=" + category + "&limit=3&page=" + page;
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        List<NewsArticle> result = newsService.getTopStories(locale, category, page);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        
        NewsArticle firstArticle = result.get(0);
        assertEquals("Test Article 1", firstArticle.getTitle());
        assertEquals("Description 1", firstArticle.getDescription());
        assertEquals("https://example.com/article1", firstArticle.getUrl());
        
        verify(restTemplate).getForObject(expectedUrl, String.class);
    }

    @Test
    void testGetTopStories_NullLocale_DefaultsToUS() {
        String mockJsonResponse = "{\"data\": []}";
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        List<NewsArticle> result = newsService.getTopStories(null, "general", 1);
        
        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, String.class);
    }

    @Test
    void testGetTopStories_APIFailure_ReturnsEmptyList() {
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class))
                .thenThrow(new RestClientException("API unavailable"));
        
        List<NewsArticle> result = newsService.getTopStories("us", "general", 1);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTopStories_EmptyDataArray() {
        String mockJsonResponse = "{\"data\": []}";
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        List<NewsArticle> result = newsService.getTopStories("us", "general", 1);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
