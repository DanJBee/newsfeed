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
     * Handles GET requests to /news endpoint with optional locale and category parameters.
     * 
     * Retrieves top news stories for the specified region and category, adding them to the model
     * for rendering in the Thymeleaf template. Both filters can be applied simultaneously.
     * 
     * Example URLs:
     * - /news (defaults to US, general)
     * - /news?locale=gb (UK news, general category)
     * - /news?category=business (US business news)
     * - /news?locale=au&category=technology (Australian technology news)
     * 
     * @param locale Optional country/region code (defaults to "us")
     * @param category Optional news category (defaults to "general")
     * @param model Spring MVC model to pass data to the view
     * @return Name of the Thymeleaf template to render ("news")
     */
    @GetMapping("/news")
    public String getNews(
            @RequestParam(value = "locale", required = false, defaultValue = "us") String locale,
            @RequestParam(value = "category", required = false, defaultValue = "general") String category,
            Model model) {
        
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
        
        // Fetch articles from service layer for the specified locale and category
        model.addAttribute("articles", newsService.getTopStories(locale, category));
        model.addAttribute("regions", availableRegions);
        model.addAttribute("categories", availableCategories);
        model.addAttribute("selectedLocale", locale);
        model.addAttribute("selectedCategory", category);
        
        // Return view name - Spring resolves this to src/main/resources/templates/news.html
        return "news";
    }
}
