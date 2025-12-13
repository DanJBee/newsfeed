package danjbee.fsp.controller;

import danjbee.fsp.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller for handling news-related HTTP requests.
 * 
 * This class follows the MVC (Model-View-Controller) pattern:
 * - Receives HTTP requests
 * - Delegates business logic to the service layer
 * - Passes data to the view (Thymeleaf template)
 * 
 * Uses constructor injection for dependencies - recommended best practice
 * as it makes dependencies explicit and enables easier testing.
 * 
 * @author Daniel Bee
 */
@Controller
public class NewsController {

    // Final field ensures immutability - dependency can't be changed after construction
    private final NewsService newsService;

    /**
     * Constructor injection of NewsService.
     * 
     * Spring automatically detects and injects the NewsService implementation.
     * This approach is preferred over field injection as it:
     * - Makes dependencies explicit
     * - Enables testing with mock services
     * - Ensures required dependencies are provided
     * 
     * @param newsService The news service implementation
     */
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Handles GET requests to /news endpoint with optional locale, category and page parameters.
     * 
     * Retrieves news stories for the specified region, category and page number, adding them to the model
     * for rendering in the Thymeleaf template. All filters can be applied simultaneously with pagination.
     * 
     * Example URLs:
     * - /news (defaults to US, general, page 1)
     * - /news?locale=gb (UK news, general category, page 1)
     * - /news?category=business (US business news, page 1)
     * - /news?locale=au&category=technology (Australian technology news, page 1)
     * - /news?locale=au&category=technology&page=2 (Australian technology news, page 2)
     * 
     * @param locale Optional country/region code (defaults to "us")
     * @param category Optional news category (defaults to "general")
     * @param page Optional page number (defaults to 1)
     * @param model Spring MVC model to pass data to the view
     * @return Name of the Thymeleaf template to render ("news")
     */
    @GetMapping("/news")
    public String getNews(
            @RequestParam(value = "locale", required = false, defaultValue = "us") String locale,
            @RequestParam(value = "category", required = false, defaultValue = "general") String category,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            Model model) {
        
        // Ensure page number is at least 1
        if (page < 1) {
            page = 1;
        }
        
        // Provide a curated list of popular regions for the dropdown
        // Using LinkedHashMap to maintain insertion order in the UI
        Map<String, String> availableRegions = new LinkedHashMap<>();
        availableRegions.put("us", "United States");
        availableRegions.put("gb", "United Kingdom");
        availableRegions.put("au", "Australia");
        availableRegions.put("ca", "Canada");
        availableRegions.put("in", "India");
        availableRegions.put("ie", "Ireland");
        availableRegions.put("nz", "New Zealand");
        availableRegions.put("sg", "Singapore");
        
        // Provide a list of news categories
        // These categories are supported by The News API
        Map<String, String> availableCategories = new LinkedHashMap<>();
        availableCategories.put("general", "General");
        availableCategories.put("business", "Business");
        availableCategories.put("entertainment", "Entertainment");
        availableCategories.put("health", "Health");
        availableCategories.put("science", "Science");
        availableCategories.put("sports", "Sports");
        availableCategories.put("technology", "Technology");
        
        // Fetch articles from service layer for the specified locale, category, and page
        model.addAttribute("articles", newsService.getTopStories(locale, category, page));
        model.addAttribute("regions", availableRegions);
        model.addAttribute("categories", availableCategories);
        model.addAttribute("selectedLocale", locale);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("currentPage", page);
        model.addAttribute("previousPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        
        // Return view name - Spring resolves this to src/main/resources/templates/news.html
        return "news";
    }
}
