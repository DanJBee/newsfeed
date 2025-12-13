package danjbee.fsp.service;

import danjbee.fsp.model.NewsArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
 * Tests verify:
 * - Correct API URL construction with various parameter combinations
 * - JSON parsing and mapping to NewsArticle objects
 * - Default parameter handling (null/empty locale, category and invalid page)
 * - Error handling when API calls fail
 * - Edge cases such as empty responses and malformed JSON
 * 
 * Uses Mockito to mock external dependencies (RestTemplate) for isolated unit testing.
 * This ensures tests are fast, reliable and don't depend on external API availability.
 * 
 * @author Daniel Bee
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NewsServiceImpl Unit Tests")
class NewsServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    private NewsServiceImpl newsService;

    private final String testToken = "test-api-token";
    
    /**
     * Sets up test dependencies before each test.
     * Injects the test API token using reflection to avoid needing properties file.
     */
    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(restTemplate);
        ReflectionTestUtils.setField(newsService, "token", testToken);
    }

    /**
     * Tests successful retrieval of news articles with valid parameters.
     * Verifies that the service correctly parses JSON and maps to NewsArticle objects.
     */
    @Test
    @DisplayName("Should successfully fetch and parse news articles with valid parameters")
    void testGetTopStories_Success() {
        // Arrange
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
        
        // Act
        List<NewsArticle> result = newsService.getTopStories(locale, category, page);
        
        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return 2 articles");
        
        NewsArticle firstArticle = result.get(0);
        assertEquals("Test Article 1", firstArticle.getTitle());
        assertEquals("Description 1", firstArticle.getDescription());
        assertEquals("https://example.com/article1", firstArticle.getUrl());
        assertEquals("https://example.com/image1.jpg", firstArticle.getImageUrl());
        assertEquals("2025-12-13T10:00:00Z", firstArticle.getPublishedAt());
        assertEquals("Test Source 1", firstArticle.getSource());
        
        verify(restTemplate, times(1)).getForObject(expectedUrl, String.class);
    }

    /**
     * Tests default locale handling when null locale is provided.
     * Service should default to "us" for null or empty locale.
     */
    @Test
    @DisplayName("Should default to 'us' locale when null locale provided")
    void testGetTopStories_NullLocale_DefaultsToUS() {
        // Arrange
        String mockJsonResponse = "{\"data\": []}";
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        // Act
        List<NewsArticle> result = newsService.getTopStories(null, "general", 1);
        
        // Assert
        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, String.class);
    }

    /**
     * Tests default locale handling when empty locale is provided.
     * Service should default to "us" for empty locale.
     */
    @Test
    @DisplayName("Should default to 'us' locale when empty locale provided")
    void testGetTopStories_EmptyLocale_DefaultsToUS() {
        // Arrange
        String mockJsonResponse = "{\"data\": []}";
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        // Act
        List<NewsArticle> result = newsService.getTopStories("  ", "general", 1);
        
        // Assert
        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, String.class);
    }

    /**
     * Tests default category handling when null category is provided.
     * Service should default to "general" for null or empty category.
     */
    @Test
    @DisplayName("Should default to 'general' category when null category provided")
    void testGetTopStories_NullCategory_DefaultsToGeneral() {
        // Arrange
        String mockJsonResponse = "{\"data\": []}";
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        // Act
        List<NewsArticle> result = newsService.getTopStories("us", null, 1);
        
        // Assert
        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, String.class);
    }

    /**
     * Tests default category handling when empty category is provided.
     * Service should default to "general" for empty category.
     */
    @Test
    @DisplayName("Should default to 'general' category when empty category provided")
    void testGetTopStories_EmptyCategory_DefaultsToGeneral() {
        // Arrange
        String mockJsonResponse = "{\"data\": []}";
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        // Act
        List<NewsArticle> result = newsService.getTopStories("us", "  ", 1);
        
        // Assert
        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, String.class);
    }

    /**
     * Tests page number validation - page less than 1 should default to 1.
     * Ensures pagination cannot be negative or zero.
     */
    @Test
    @DisplayName("Should default to page 1 when page number is less than 1")
    void testGetTopStories_InvalidPage_DefaultsToOne() {
        // Arrange
        String mockJsonResponse = "{\"data\": []}";
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        // Act
        List<NewsArticle> result = newsService.getTopStories("us", "general", 0);
        
        // Assert
        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, String.class);
    }

    /**
     * Tests pagination with page 2.
     * Verifies correct page parameter is passed to API.
     */
    @Test
    @DisplayName("Should correctly handle pagination for page 2")
    void testGetTopStories_Page2() {
        // Arrange
        String mockJsonResponse = "{\"data\": []}";
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=gb&categories=technology&limit=3&page=2";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        // Act
        List<NewsArticle> result = newsService.getTopStories("gb", "technology", 2);
        
        // Assert
        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrl, String.class);
    }

    /**
     * Tests error handling when RestTemplate throws an exception.
     * Service should catch the exception and return an empty list rather than crashing.
     */
    @Test
    @DisplayName("Should return empty list when API call fails")
    void testGetTopStories_APIFailure_ReturnsEmptyList() {
        // Arrange
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class))
                .thenThrow(new RestClientException("API unavailable"));
        
        // Act
        List<NewsArticle> result = newsService.getTopStories("us", "general", 1);
        
        // Assert
        assertNotNull(result, "Result should not be null even on error");
        assertTrue(result.isEmpty(), "Result should be empty list on error");
        verify(restTemplate).getForObject(expectedUrl, String.class);
    }

    /**
     * Tests handling of empty data array in API response.
     * Service should return an empty list when no articles are available.
     */
    @Test
    @DisplayName("Should return empty list when API returns empty data array")
    void testGetTopStories_EmptyDataArray() {
        // Arrange
        String mockJsonResponse = "{\"data\": []}";
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        // Act
        List<NewsArticle> result = newsService.getTopStories("us", "general", 1);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Should return empty list when data array is empty");
    }

    /**
     * Tests handling of missing data field in API response.
     * Service should gracefully handle malformed JSON structure.
     */
    @Test
    @DisplayName("Should return empty list when API response has no data field")
    void testGetTopStories_MissingDataField() {
        // Arrange
        String mockJsonResponse = "{\"error\": \"Something went wrong\"}";
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        // Act
        List<NewsArticle> result = newsService.getTopStories("us", "general", 1);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Should return empty list when data field is missing");
    }

    /**
     * Tests handling of partial article data.
     * Service should handle missing fields gracefully with empty strings.
     */
    @Test
    @DisplayName("Should handle articles with missing fields gracefully")
    void testGetTopStories_PartialArticleData() {
        // Arrange
        String mockJsonResponse = """
                {
                    "data": [
                        {
                            "title": "Test Article",
                            "url": "https://example.com/article"
                        }
                    ]
                }
                """;
        
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        // Act
        List<NewsArticle> result = newsService.getTopStories("us", "general", 1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        NewsArticle article = result.get(0);
        assertEquals("Test Article", article.getTitle());
        assertEquals("https://example.com/article", article.getUrl());
        assertEquals("", article.getDescription(), "Missing fields should default to empty string");
        assertEquals("", article.getImageUrl(), "Missing fields should default to empty string");
    }

    /**
     * Tests correct URL construction with different locales.
     * Verifies locale parameter is correctly appended to API URL.
     */
    @Test
    @DisplayName("Should construct correct API URL for different locales")
    void testGetTopStories_DifferentLocales() {
        // Test Australian locale
        String mockJsonResponse = "{\"data\": []}";
        String expectedUrlAU = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=au&categories=sports&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrlAU, String.class)).thenReturn(mockJsonResponse);
        
        List<NewsArticle> result = newsService.getTopStories("au", "sports", 1);
        
        assertNotNull(result);
        verify(restTemplate).getForObject(expectedUrlAU, String.class);
    }

    /**
     * Tests that multiple articles are correctly parsed and returned.
     * Verifies the service can handle responses with multiple articles.
     */
    @Test
    @DisplayName("Should correctly parse multiple articles from API response")
    void testGetTopStories_MultipleArticles() {
        // Arrange
        String mockJsonResponse = """
                {
                    "data": [
                        {
                            "title": "Article 1",
                            "description": "Desc 1",
                            "url": "https://example.com/1",
                            "image_url": "https://example.com/img1.jpg",
                            "published_at": "2025-12-13T10:00:00Z",
                            "source": "Source 1"
                        },
                        {
                            "title": "Article 2",
                            "description": "Desc 2",
                            "url": "https://example.com/2",
                            "image_url": "https://example.com/img2.jpg",
                            "published_at": "2025-12-13T11:00:00Z",
                            "source": "Source 2"
                        },
                        {
                            "title": "Article 3",
                            "description": "Desc 3",
                            "url": "https://example.com/3",
                            "image_url": "https://example.com/img3.jpg",
                            "published_at": "2025-12-13T12:00:00Z",
                            "source": "Source 3"
                        }
                    ]
                }
                """;
        
        String expectedUrl = "https://api.thenewsapi.com/v1/news/top?api_token=" + testToken 
                + "&locale=us&categories=general&limit=3&page=1";
        
        when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockJsonResponse);
        
        // Act
        List<NewsArticle> result = newsService.getTopStories("us", "general", 1);
        
        // Assert
        assertNotNull(result);
        assertEquals(3, result.size(), "Should return all 3 articles from response");
        assertEquals("Article 1", result.get(0).getTitle());
        assertEquals("Article 2", result.get(1).getTitle());
        assertEquals("Article 3", result.get(2).getTitle());
    }
}
