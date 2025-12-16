package danjbee.fsp.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
 * Tests are ordered to maximise cache efficiency and minimise API calls.
 * Cache keys are based on locale, category and page number combinations.
 * 
 * @author Daniel Bee
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
    void testNewsEndpoint_DefaultParameters() {
        // Test the /news endpoint with default parameters (us-general-1)
        // This will make the first API call and populate the cache
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
    @Order(2)
    void testNewsEndpoint_CustomLocale() {
        // Test with UK locale (gb-general-1)
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
    @Order(3)
    void testNewsEndpoint_CustomCategory() {
        // Test with business category (us-business-1)
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
    @Order(4)
    void testNewsEndpoint_WithPagination() {
        // Test pagination functionality (us-general-2)
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/news?page=2",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(5)
    void testNewsEndpoint_MultipleParameters() {
        // Test with multiple query parameters (au-technology-1)
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
    @Order(6)
    void testNewsEndpoint_InvalidPageNumber() {
        // Test that invalid page numbers are handled gracefully
        // Should default to page 1 (us-general-1, uses cached data)
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/news?page=0",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(7)
    void testNewsEndpoint_AllRegions() {
        // Test all available regions to ensure they work
        // First 3 regions reuse cached data: us, gb, au already tested
        // Only 5 new API calls needed for: ca, in, ie, nz, sg
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
    @Order(8)
    void testNewsEndpoint_AllCategories() {
        // Test all available categories
        // First 3 categories reuse cached data: general, business, technology already tested
        // Only 4 new API calls needed for: entertainment, health, science, sports
        String[] categories = {"general", "business", "technology", "entertainment", 
                "health", "science", "sports"};
        
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
    @Order(9)
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
    @Order(10)
    void testApplicationContextLoads() {
        // Verify Spring context loads successfully
        assertNotNull(restTemplate, "RestTemplate should be initialised");
        assertTrue(port > 0, "Server port should be assigned");
    }
}
