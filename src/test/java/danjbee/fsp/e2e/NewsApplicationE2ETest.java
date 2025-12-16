package danjbee.fsp.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the News Application.
 * 
 * These tests verify that the entire application stack works correctly
 * including the controller, service layer and external API integration.
 * Tests are run against a real embedded server instance.
 * 
 * @author Daniel Bee
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NewsApplicationE2ETest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        restTemplate = new RestTemplate();
    }

    @Test
    void testNewsEndpoint_DefaultParameters() {
        // Test the /news endpoint with default parameters
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/news",
                String.class
        );

        // Verify response status is OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify response body contains expected content
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("News"), "Response should contain 'News'");
        assertTrue(body.contains("United States") || body.contains("General"), 
                "Response should contain region or category information");
    }

    @Test
    void testNewsEndpoint_CustomLocale() {
        // Test with UK locale
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/news?locale=gb",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("United Kingdom"), 
                "Response should contain United Kingdom when locale is gb");
    }

    @Test
    void testNewsEndpoint_CustomCategory() {
        // Test with business category
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/news?category=business",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("Business"), 
                "Response should contain Business category");
    }

    @Test
    void testNewsEndpoint_WithPagination() {
        // Test pagination functionality
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/news?page=2",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testNewsEndpoint_MultipleParameters() {
        // Test with multiple query parameters
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/news?locale=au&category=technology&page=1",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("Australia"), 
                "Response should contain Australia when locale is au");
        assertTrue(body.contains("Technology"), 
                "Response should contain Technology category");
    }

    @Test
    void testNewsEndpoint_InvalidPageNumber() {
        // Test that invalid page numbers are handled gracefully
        // Should default to page 1
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/news?page=0",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testNewsEndpoint_AllRegions() {
        // Test all available regions to ensure they work
        String[] regions = {"us", "gb", "au", "ca", "in", "ie", "nz", "sg"};
        
        for (String region : regions) {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/news?locale=" + region,
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode(),
                    "Region " + region + " should return OK status");
            assertNotNull(response.getBody(),
                    "Region " + region + " should return a non-null body");
        }
    }

    @Test
    void testNewsEndpoint_AllCategories() {
        // Test all available categories
        String[] categories = {"general", "business", "entertainment", 
                "health", "science", "sports", "technology"};
        
        for (String category : categories) {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/news?category=" + category,
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode(),
                    "Category " + category + " should return OK status");
            assertNotNull(response.getBody(),
                    "Category " + category + " should return a non-null body");
        }
    }

    @Test
    void testRootRedirect() {
        // Test if root URL redirects or serves content
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + "/",
                    String.class
            );
            
            // Should either redirect or return success
            assertTrue(response.getStatusCode().is3xxRedirection() ||
                       response.getStatusCode().is2xxSuccessful());
        } catch (Exception e) {
            // 404 is expected for root endpoint as only /news is configured
            assertTrue(e.getMessage().contains("404") || e.getMessage().contains("Not Found"),
                    "Root endpoint should return 404 when not configured");
        }
    }

    @Test
    void testApplicationContextLoads() {
        // Verify Spring context loads successfully
        assertNotNull(restTemplate, "RestTemplate should be initialised");
        assertTrue(port > 0, "Server port should be assigned");
    }
}
