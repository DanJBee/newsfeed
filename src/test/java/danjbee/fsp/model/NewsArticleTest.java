package danjbee.fsp.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NewsArticle model.
 * 
 * Tests verify:
 * - Getter and setter functionality (provided by Lombok @Data)
 * - Object equality (equals and hashCode)
 * - toString method
 * - Null value handling
 * 
 * Although NewsArticle uses Lombok's @Data annotation which generates these methods,
 * testing ensures the annotation is properly configured and the model behaves as expected.
 * 
 * @author Daniel Bee
 */
@DisplayName("NewsArticle Model Tests")
class NewsArticleTest {

    /**
     * Tests that NewsArticle can be instantiated and setters work correctly.
     */
    @Test
    @DisplayName("Should create NewsArticle with all fields")
    void testNewsArticle_AllFields() {
        // Arrange & Act
        NewsArticle article = new NewsArticle();
        article.setTitle("Test Title");
        article.setDescription("Test Description");
        article.setUrl("https://example.com/article");
        article.setImageUrl("https://example.com/image.jpg");
        article.setPublishedAt("2025-12-13T10:00:00Z");
        article.setSource("Test Source");
        
        // Assert
        assertEquals("Test Title", article.getTitle());
        assertEquals("Test Description", article.getDescription());
        assertEquals("https://example.com/article", article.getUrl());
        assertEquals("https://example.com/image.jpg", article.getImageUrl());
        assertEquals("2025-12-13T10:00:00Z", article.getPublishedAt());
        assertEquals("Test Source", article.getSource());
    }

    /**
     * Tests that two NewsArticle objects with same values are equal.
     * Lombok's @Data should generate proper equals() method.
     */
    @Test
    @DisplayName("Should correctly implement equals for identical articles")
    void testNewsArticle_Equals() {
        // Arrange
        NewsArticle article1 = new NewsArticle();
        article1.setTitle("Title");
        article1.setDescription("Description");
        article1.setUrl("https://example.com");
        
        NewsArticle article2 = new NewsArticle();
        article2.setTitle("Title");
        article2.setDescription("Description");
        article2.setUrl("https://example.com");
        
        // Assert
        assertEquals(article1, article2, "Articles with same values should be equal");
    }

    /**
     * Tests that two NewsArticle objects with different values are not equal.
     */
    @Test
    @DisplayName("Should correctly identify non-equal articles")
    void testNewsArticle_NotEquals() {
        // Arrange
        NewsArticle article1 = new NewsArticle();
        article1.setTitle("Title 1");
        article1.setUrl("https://example.com/1");
        
        NewsArticle article2 = new NewsArticle();
        article2.setTitle("Title 2");
        article2.setUrl("https://example.com/2");
        
        // Assert
        assertNotEquals(article1, article2, "Articles with different values should not be equal");
    }

    /**
     * Tests that hashCode is consistent with equals.
     * Equal objects must have equal hash codes.
     */
    @Test
    @DisplayName("Should generate consistent hashCode for equal articles")
    void testNewsArticle_HashCode() {
        // Arrange
        NewsArticle article1 = new NewsArticle();
        article1.setTitle("Title");
        article1.setDescription("Description");
        
        NewsArticle article2 = new NewsArticle();
        article2.setTitle("Title");
        article2.setDescription("Description");
        
        // Assert
        assertEquals(article1.hashCode(), article2.hashCode(), 
                "Equal articles should have same hash code");
    }

    /**
     * Tests that toString method produces readable output.
     * Lombok's @Data generates toString with all fields.
     */
    @Test
    @DisplayName("Should generate meaningful toString representation")
    void testNewsArticle_ToString() {
        // Arrange
        NewsArticle article = new NewsArticle();
        article.setTitle("Test Title");
        article.setUrl("https://example.com");
        
        // Act
        String toString = article.toString();
        
        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("Test Title"), "toString should include title");
        assertTrue(toString.contains("https://example.com"), "toString should include URL");
    }

    /**
     * Tests handling of null values in fields.
     * Model should accept null values without throwing exceptions.
     */
    @Test
    @DisplayName("Should handle null values in fields")
    void testNewsArticle_NullValues() {
        // Arrange & Act
        NewsArticle article = new NewsArticle();
        article.setTitle(null);
        article.setDescription(null);
        article.setUrl(null);
        article.setImageUrl(null);
        article.setPublishedAt(null);
        article.setSource(null);
        
        // Assert
        assertNull(article.getTitle());
        assertNull(article.getDescription());
        assertNull(article.getUrl());
        assertNull(article.getImageUrl());
        assertNull(article.getPublishedAt());
        assertNull(article.getSource());
    }

    /**
     * Tests that empty strings are handled correctly.
     * Model should store empty strings as provided.
     */
    @Test
    @DisplayName("Should handle empty string values")
    void testNewsArticle_EmptyStrings() {
        // Arrange & Act
        NewsArticle article = new NewsArticle();
        article.setTitle("");
        article.setDescription("");
        article.setUrl("");
        
        // Assert
        assertEquals("", article.getTitle());
        assertEquals("", article.getDescription());
        assertEquals("", article.getUrl());
    }

    /**
     * Tests that a newly created NewsArticle has null values.
     * Verifies default state of the object.
     */
    @Test
    @DisplayName("Should initialise with null values")
    void testNewsArticle_DefaultValues() {
        // Arrange & Act
        NewsArticle article = new NewsArticle();
        
        // Assert
        assertNull(article.getTitle(), "Title should be null by default");
        assertNull(article.getDescription(), "Description should be null by default");
        assertNull(article.getUrl(), "URL should be null by default");
        assertNull(article.getImageUrl(), "Image URL should be null by default");
        assertNull(article.getPublishedAt(), "Published date should be null by default");
        assertNull(article.getSource(), "Source should be null by default");
    }

    /**
     * Tests reflexive property of equals (x.equals(x) should be true).
     */
    @Test
    @DisplayName("Should satisfy reflexive property of equals")
    void testNewsArticle_EqualsReflexive() {
        // Arrange
        NewsArticle article = new NewsArticle();
        article.setTitle("Title");
        
        // Assert
        assertEquals(article, article, "Article should equal itself");
    }

    /**
     * Tests symmetric property of equals (if x.equals(y), then y.equals(x)).
     */
    @Test
    @DisplayName("Should satisfy symmetric property of equals")
    void testNewsArticle_EqualsSymmetric() {
        // Arrange
        NewsArticle article1 = new NewsArticle();
        article1.setTitle("Title");
        
        NewsArticle article2 = new NewsArticle();
        article2.setTitle("Title");
        
        // Assert
        assertEquals(article1, article2);
        assertEquals(article2, article1, "Equals should be symmetric");
    }

    /**
     * Tests that equals returns false when comparing with null.
     */
    @Test
    @DisplayName("Should return false when comparing with null")
    void testNewsArticle_EqualsNull() {
        // Arrange
        NewsArticle article = new NewsArticle();
        article.setTitle("Title");
        
        // Assert
        assertNotEquals(null, article, "Article should not equal null");
    }

    /**
     * Tests that equals returns false when comparing with different type.
     */
    @Test
    @DisplayName("Should return false when comparing with different type")
    void testNewsArticle_EqualsDifferentType() {
        // Arrange
        NewsArticle article = new NewsArticle();
        article.setTitle("Title");
        String notAnArticle = "Not an article";
        
        // Assert
        assertNotEquals(article, notAnArticle, "Article should not equal String");
    }
}
