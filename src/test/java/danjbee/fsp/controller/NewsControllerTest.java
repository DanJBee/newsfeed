package danjbee.fsp.controller;

import danjbee.fsp.model.NewsArticle;
import danjbee.fsp.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for NewsController.
 * 
 * Tests verify:
 * - Correct handling of GET requests to /news endpoint
 * - Default parameter values (locale, category and page)
 * - Model attribute population for view rendering
 * - Filter combination handling (region + category + page)
 * - Pagination navigation (previous/next page calculations)
 * - Correct view name returned
 * 
 * Uses MockMvc for testing Spring MVC controllers without starting a full server.
 * This provides fast, focused testing of controller behaviour.
 * 
 * @author Daniel Bee
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NewsController Unit Tests")
class NewsControllerTest {

    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsController newsController;

    private MockMvc mockMvc;

    private List<NewsArticle> mockArticles;

    /**
     * Sets up test environment before each test.
     * Initialises MockMvc and creates mock article data.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(newsController)
                .setViewResolvers((viewName, locale) -> {
                    // Create a simple view that doesn't actually render
                    return (model, request, response) -> {};
                })
                .build();
        
        // Create mock articles for testing
        mockArticles = new ArrayList<>();
        NewsArticle article1 = new NewsArticle();
        article1.setTitle("Test Article 1");
        article1.setDescription("Description 1");
        article1.setUrl("https://example.com/1");
        
        NewsArticle article2 = new NewsArticle();
        article2.setTitle("Test Article 2");
        article2.setDescription("Description 2");
        article2.setUrl("https://example.com/2");
        
        mockArticles.add(article1);
        mockArticles.add(article2);
    }

    /**
     * Tests GET request to /news with default parameters.
     * Should default to US locale, general category and page 1.
     */
    @Test
    @DisplayName("Should handle GET request with default parameters")
    void testGetNews_DefaultParameters() throws Exception {
        // Arrange
        when(newsService.getTopStories("us", "general", 1)).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(view().name("news"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("regions"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("selectedLocale", "us"))
                .andExpect(model().attribute("selectedCategory", "general"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("previousPage", 0))
                .andExpect(model().attribute("nextPage", 2));
        
        verify(newsService, times(1)).getTopStories("us", "general", 1);
    }

    /**
     * Tests GET request with custom locale parameter.
     * Verifies locale is correctly passed to service and reflected in model.
     */
    @Test
    @DisplayName("Should handle GET request with custom locale")
    void testGetNews_CustomLocale() throws Exception {
        // Arrange
        when(newsService.getTopStories("gb", "general", 1)).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news").param("locale", "gb"))
                .andExpect(status().isOk())
                .andExpect(view().name("news"))
                .andExpect(model().attribute("selectedLocale", "gb"))
                .andExpect(model().attribute("selectedCategory", "general"));
        
        verify(newsService).getTopStories("gb", "general", 1);
    }

    /**
     * Tests GET request with custom category parameter.
     * Verifies category is correctly passed to service and reflected in model.
     */
    @Test
    @DisplayName("Should handle GET request with custom category")
    void testGetNews_CustomCategory() throws Exception {
        // Arrange
        when(newsService.getTopStories("us", "business", 1)).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news").param("category", "business"))
                .andExpect(status().isOk())
                .andExpect(view().name("news"))
                .andExpect(model().attribute("selectedLocale", "us"))
                .andExpect(model().attribute("selectedCategory", "business"));
        
        verify(newsService).getTopStories("us", "business", 1);
    }

    /**
     * Tests GET request with both locale and category parameters.
     * Verifies multiple filters work together correctly.
     */
    @Test
    @DisplayName("Should handle GET request with both locale and category")
    void testGetNews_LocaleAndCategory() throws Exception {
        // Arrange
        when(newsService.getTopStories("au", "technology", 1)).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news")
                .param("locale", "au")
                .param("category", "technology"))
                .andExpect(status().isOk())
                .andExpect(view().name("news"))
                .andExpect(model().attribute("selectedLocale", "au"))
                .andExpect(model().attribute("selectedCategory", "technology"))
                .andExpect(model().attribute("currentPage", 1));
        
        verify(newsService).getTopStories("au", "technology", 1);
    }

    /**
     * Tests GET request with page parameter.
     * Verifies pagination is correctly handled.
     */
    @Test
    @DisplayName("Should handle GET request with page parameter")
    void testGetNews_WithPageParameter() throws Exception {
        // Arrange
        when(newsService.getTopStories("us", "general", 2)).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("news"))
                .andExpect(model().attribute("currentPage", 2))
                .andExpect(model().attribute("previousPage", 1))
                .andExpect(model().attribute("nextPage", 3));
        
        verify(newsService).getTopStories("us", "general", 2);
    }

    /**
     * Tests GET request with all parameters (locale, category and page).
     * Verifies all filters work together with pagination.
     */
    @Test
    @DisplayName("Should handle GET request with all parameters")
    void testGetNews_AllParameters() throws Exception {
        // Arrange
        when(newsService.getTopStories("gb", "technology", 3)).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news")
                .param("locale", "gb")
                .param("category", "technology")
                .param("page", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("news"))
                .andExpect(model().attribute("selectedLocale", "gb"))
                .andExpect(model().attribute("selectedCategory", "technology"))
                .andExpect(model().attribute("currentPage", 3))
                .andExpect(model().attribute("previousPage", 2))
                .andExpect(model().attribute("nextPage", 4));
        
        verify(newsService).getTopStories("gb", "technology", 3);
    }

    /**
     * Tests page validation - page less than 1 should default to 1.
     * Ensures invalid page numbers are corrected.
     */
    @Test
    @DisplayName("Should default to page 1 when invalid page number provided")
    void testGetNews_InvalidPage_DefaultsToOne() throws Exception {
        // Arrange
        when(newsService.getTopStories(anyString(), anyString(), anyInt())).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("news"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("previousPage", 0))
                .andExpect(model().attribute("nextPage", 2));
        
        verify(newsService).getTopStories("us", "general", 1);
    }

    /**
     * Tests that regions attribute contains expected region mappings.
     * Verifies all supported regions are available in the model.
     */
    @Test
    @DisplayName("Should populate regions attribute with all available regions")
    void testGetNews_RegionsAttribute() throws Exception {
        // Arrange
        when(newsService.getTopStories(anyString(), anyString(), anyInt())).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("regions", hasKey("us")))
                .andExpect(model().attribute("regions", hasKey("gb")))
                .andExpect(model().attribute("regions", hasKey("au")))
                .andExpect(model().attribute("regions", hasKey("ca")))
                .andExpect(model().attribute("regions", hasKey("in")))
                .andExpect(model().attribute("regions", hasKey("ie")))
                .andExpect(model().attribute("regions", hasKey("nz")))
                .andExpect(model().attribute("regions", hasKey("sg")));
    }

    /**
     * Tests that categories attribute contains expected category mappings.
     * Verifies all supported categories are available in the model.
     */
    @Test
    @DisplayName("Should populate categories attribute with all available categories")
    void testGetNews_CategoriesAttribute() throws Exception {
        // Arrange
        when(newsService.getTopStories(anyString(), anyString(), anyInt())).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("categories", hasKey("general")))
                .andExpect(model().attribute("categories", hasKey("business")))
                .andExpect(model().attribute("categories", hasKey("entertainment")))
                .andExpect(model().attribute("categories", hasKey("health")))
                .andExpect(model().attribute("categories", hasKey("science")))
                .andExpect(model().attribute("categories", hasKey("sports")))
                .andExpect(model().attribute("categories", hasKey("technology")));
    }

    /**
     * Tests that articles attribute contains the list returned by service.
     * Verifies service results are correctly passed to the view.
     */
    @Test
    @DisplayName("Should populate articles attribute with service results")
    void testGetNews_ArticlesAttribute() throws Exception {
        // Arrange
        when(newsService.getTopStories("us", "general", 1)).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("articles", hasSize(2)))
                .andExpect(model().attribute("articles", mockArticles));
    }

    /**
     * Tests handling of empty article list from service.
     * Controller should handle empty results gracefully.
     */
    @Test
    @DisplayName("Should handle empty article list from service")
    void testGetNews_EmptyArticleList() throws Exception {
        // Arrange
        when(newsService.getTopStories(anyString(), anyString(), anyInt())).thenReturn(new ArrayList<>());
        
        // Act & Assert
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(view().name("news"))
                .andExpect(model().attribute("articles", hasSize(0)));
    }

    /**
     * Tests correct view name is returned.
     * Verifies controller returns "news" to resolve to news.html template.
     */
    @Test
    @DisplayName("Should return correct view name")
    void testGetNews_ViewName() throws Exception {
        // Arrange
        when(newsService.getTopStories(anyString(), anyString(), anyInt())).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news"))
                .andExpect(view().name("news"));
    }

    /**
     * Tests pagination calculations for page 5.
     * Verifies previousPage and nextPage are correctly calculated.
     */
    @Test
    @DisplayName("Should correctly calculate pagination for higher page numbers")
    void testGetNews_HigherPageNumber() throws Exception {
        // Arrange
        when(newsService.getTopStories("us", "general", 5)).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news").param("page", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 5))
                .andExpect(model().attribute("previousPage", 4))
                .andExpect(model().attribute("nextPage", 6));
    }

    /**
     * Tests that region codes are correctly maintained as keys in regions map.
     * Verifies LinkedHashMap maintains insertion order.
     */
    @Test
    @DisplayName("Should maintain region codes as keys in regions map")
    @SuppressWarnings("unchecked")
    void testGetNews_RegionCodesAsKeys() throws Exception {
        // Arrange
        when(newsService.getTopStories(anyString(), anyString(), anyInt())).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("regions", aMapWithSize(8)))
                .andExpect(result -> {
                    Map<String, String> regions = (Map<String, String>) result.getModelAndView()
                            .getModel().get("regions");
                    assert regions.get("us").equals("United States");
                    assert regions.get("gb").equals("United Kingdom");
                    assert regions.get("au").equals("Australia");
                });
    }

    /**
     * Tests that category codes are correctly maintained as keys in categories map.
     * Verifies proper category mappings for dropdown display.
     */
    @Test
    @DisplayName("Should maintain category codes as keys in categories map")
    @SuppressWarnings("unchecked")
    void testGetNews_CategoryCodesAsKeys() throws Exception {
        // Arrange
        when(newsService.getTopStories(anyString(), anyString(), anyInt())).thenReturn(mockArticles);
        
        // Act & Assert
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("categories", aMapWithSize(7)))
                .andExpect(result -> {
                    Map<String, String> categories = (Map<String, String>) result.getModelAndView()
                            .getModel().get("categories");
                    assert categories.get("general").equals("General");
                    assert categories.get("business").equals("Business");
                    assert categories.get("technology").equals("Technology");
                });
    }
}
