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
     * Handles GET requests to /news endpoint with optional locale parameter.
     * 
     * Retrieves top news stories for the specified region and adds them to the model
     * for rendering in the Thymeleaf template.
     * 
     * Example URLs:
     * - /news (defaults to US)
     * - /news?locale=gb (UK news)
     * - /news?locale=au (Australian news)
     * 
     * @param locale Optional country/region code (defaults to "us")
     * @param model Spring MVC model to pass data to the view
     * @return Name of the Thymeleaf template to render ("news")
     */
    @GetMapping("/news")
    public String getNews(
            @RequestParam(value = "locale", required = false, defaultValue = "us") String locale,
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
        
        // Fetch articles from service layer for the specified locale
        model.addAttribute("articles", newsService.getTopStories(locale));
        model.addAttribute("regions", availableRegions);
        model.addAttribute("selectedLocale", locale);
        
        // Return view name - Spring resolves this to src/main/resources/templates/news.html
        return "news";
    }
}
