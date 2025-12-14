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
 * @author Daniel Bee
 */
@Controller
public class NewsController {

    private final NewsService newsService;

    // Constructor injection
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    // Handles GET requests to /news endpoint
    @GetMapping("/news")
    public String getNews(
            @RequestParam(value = "locale", required = false, defaultValue = "us") String locale,
            @RequestParam(value = "category", required = false, defaultValue = "general") String category,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            Model model) {
        
        if (page < 1) {
            page = 1;
        }
        
        // Available regions for dropdown
        Map<String, String> availableRegions = new LinkedHashMap<>();
        availableRegions.put("us", "United States");
        availableRegions.put("gb", "United Kingdom");
        availableRegions.put("au", "Australia");
        availableRegions.put("ca", "Canada");
        availableRegions.put("in", "India");
        availableRegions.put("ie", "Ireland");
        availableRegions.put("nz", "New Zealand");
        availableRegions.put("sg", "Singapore");
        
        // Available categories for dropdown
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
