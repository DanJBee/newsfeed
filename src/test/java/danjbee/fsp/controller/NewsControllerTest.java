package danjbee.fsp.controller;

import danjbee.fsp.model.NewsArticle;
import danjbee.fsp.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for NewsController.
 * 
 * @author Daniel Bee
 */
@ExtendWith(MockitoExtension.class)
class NewsControllerTest {

    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsController newsController;

    private MockMvc mockMvc;
    private List<NewsArticle> mockArticles;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(newsController)
                .setViewResolvers((viewName, locale) -> {
                    return (model, request, response) -> {};
                })
                .build();
        
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

    @Test
    void testGetNews_DefaultParameters() throws Exception {
        when(newsService.getTopStories("us", "general", 1)).thenReturn(mockArticles);
        
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(view().name("news"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("selectedLocale", "us"))
                .andExpect(model().attribute("selectedCategory", "general"));
        
        verify(newsService).getTopStories("us", "general", 1);
    }

    @Test
    void testGetNews_CustomLocale() throws Exception {
        when(newsService.getTopStories("gb", "general", 1)).thenReturn(mockArticles);
        
        mockMvc.perform(get("/news").param("locale", "gb"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedLocale", "gb"));
        
        verify(newsService).getTopStories("gb", "general", 1);
    }

    @Test
    void testGetNews_CustomCategory() throws Exception {
        when(newsService.getTopStories("us", "business", 1)).thenReturn(mockArticles);
        
        mockMvc.perform(get("/news").param("category", "business"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedCategory", "business"));
        
        verify(newsService).getTopStories("us", "business", 1);
    }

    @Test
    void testGetNews_WithPagination() throws Exception {
        when(newsService.getTopStories("us", "general", 2)).thenReturn(mockArticles);
        
        mockMvc.perform(get("/news").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 2));
        
        verify(newsService).getTopStories("us", "general", 2);
    }
}
