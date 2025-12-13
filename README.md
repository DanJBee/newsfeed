# News Feed Application
News project for FSP Assessment Day

**Author:** Daniel Bee  
**Date:** December 2025

## Project Overview

This is a Spring Boot web application that displays the top 3 news stories from The News API. It demonstrates clean architecture, SOLID principles, and modern Java development practices.

## Key Features

- **Regional News Filtering:** Users can select from 8 different regions (US, UK, Australia, Canada, India, Ireland, New Zealand, Singapore)
- **Smart Caching:** Region-specific caching ensures fast load times while respecting API limits
- **Responsive Design:** Clean, mobile-friendly interface
- **External Links:** Direct links to full articles on original news sources

## Architecture & Design Decisions

### Three-Layer Architecture
I structured the application using the MVC pattern with clear separation of concerns:

**Controller Layer** (`danjbee.fsp.controller`)
- Handles HTTP requests and responses
- Delegates business logic to the service layer
- Passes data to the view layer (Thymeleaf templates)

**Service Layer** (`danjbee.fsp.service`)
- Contains business logic for fetching and processing news
- Communicates with external APIs
- Interface-based design enables easy testing and future modifications

**Model Layer** (`danjbee.fsp.model`)
- Represents data structures
- Uses Lombok to reduce boilerplate code

### Key Design Patterns

**Dependency Injection**
- Used constructor injection for better testability and explicitness
- Spring automatically manages component lifecycles

**Interface Segregation (SOLID)**
- `NewsService` interface decouples controller from implementation
- Makes the code easier to test with mock implementations
- Allows for multiple implementations if needed in the future

**Caching Strategy**
- Implemented Spring Cache with Caffeine to respect API rate limits
- Cache is **locale-specific** using cache keys - ensures users can switch regions without stale data
- 24-hour cache expiration balances freshness with API conservation
- Graceful degradation: returns empty list on API failure rather than crashing

**Query Parameter Handling**
- Uses `@RequestParam` with default value for clean URL structure
- Supports both `/news` (defaults to US) and `/news?locale=gb` patterns
- LinkedHashMap maintains consistent region ordering in dropdown

## Technologies Used

- **Spring Boot 4.0.0** - Application framework
- **Spring Web MVC** - Web layer
- **Thymeleaf** - Server-side templating engine
- **Spring Cache + Caffeine** - Caching abstraction
- **Jackson** - JSON parsing
- **Lombok** - Reduces boilerplate code
- **Maven** - Build tool

## Project Structure

```
src/main/java/danjbee/fsp/
├── NewsApplication.java          # Main application entry point
├── config/
│   └── CacheConfig.java         # Cache configuration
├── controller/
│   └── NewsController.java      # Handles /news endpoint
├── service/
│   ├── NewsService.java         # Service interface
│   └── NewsServiceImpl.java     # Service implementation
└── model/
    └── NewsArticle.java         # News article data model

src/main/resources/
├── application.properties       # Application configuration
├── token.properties            # API token (excluded from git)
└── templates/
    └── news.html              # Thymeleaf template for displaying news
```

## How to Run

1. **Prerequisites:**
   - Java 17 or higher
   - Maven

2. **Add your API token:**
   Create `src/main/resources/token.properties`:
   ```
   token=YOUR_API_TOKEN_HERE
   ```

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or on Windows:
   
   You can also specify a region directly in the URL:
   - `http://localhost:8080/news?locale=gb` (UK news)
   - `http://localhost:8080/news?locale=au` (Australian news)
   - `http://localhost:8080/news?locale=ca` (Canadian news)

## How It Worksboot:run
   ```

4. **Access the application:**
   Open your browser to: `http://localhost:8080/news`
Advanced Caching:** Implementing locale-specific caching with custom cache keys
- **Query Parameters:** Using `@RequestParam` for clean, RESTful URL design
- **Thymeleaf Templating:** Building dynamic web pages with server-side rendering, conditionals, and iteration
- **State Management:** Maintaining selected region state across page loads
- **Clean Architecture:** Separating concerns across layers for maintainability and testability
- **Error Handling:** Gracefully handling API failures without crashing the application
- **User Experience:** Creating intuitive region filtering with immediate visual feedbackand property injection
- **RESTful API Integration:** Making HTTP requests with RestTemplate and parsing JSON responses
- **Caching Strategies:** Implementing application-level caching to improve performance and respect API limits
- **Thymeleaf Templating:** Building dynamic web pages with server-side rendering
- **Clean Architecture:** Separating concerns across layers for maintainability and testability
- **Error Handling:** Gracefully handling API failures without crashing the application

## Future Enhancements

If I had more time, I would add:
- Unit tests for service layer using JUnit and Mockito
- Integration tests for the controller
- Pagination for viewing more than 3 articles
- Search and filter functionality
- Proper logging with SLF4J instead of System.err
- Error page for when API fails
- Responsive design improvements for mobile devices

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [The News API Documentation](https://www.thenewsapi.com/documentation)
- [Caffeine Caching Library](https://github.com/ben-manes/caffeine)
