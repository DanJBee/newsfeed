# News Feed Application
News project for FSP Assessment Day

**Author:** Daniel Bee

## Project Overview

This is a Spring Boot web application that displays the top 3 news stories from The News API. It demonstrates clean architecture, SOLID principles and modern Java development practices.

## Key Features

- **Regional News Filtering:** Users can select from 8 different regions (US, UK, Australia, Canada, India, Ireland, New Zealand and Singapore)
- **Category Filtering:** Users can filter by 7 news categories (General, Business, Entertainment, Health, Science, Sports and Technology)
- **Pagination:** Navigate through multiple pages of news articles (3 articles per page)
- **Combined Filtering:** Region, category and pagination work simultaneously for precise news discovery
- **Smart Caching:** Region, category and page-specific caching ensures fast load times whilst respecting API limits
- **Responsive Design:** Clean, mobile-friendly interface with flexible filter and pagination layout
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
- Cache is **combination-specific** using composite keys (locale + category + page)
- Cache key format: `"us-business-1"`, `"gb-technology-2"`, etc.
- Ensures users can switch filters or pages without stale data
- 24-hour cache expiration balances freshness with API conservation
- Graceful degradation: returns empty list on API failure rather than crashing

**Query Parameter Handling**
- Uses `@RequestParam` with default values for clean URL structure
- Supports multiple filter combinations and pagination via query parameters
- Example: `/news?locale=gb&category=technology&page=2` (UK technology news, page 2)
- All parameters are optional with sensible defaults (US, General, Page 1)
- LinkedHashMap maintains consistent filter ordering in dropdowns

**Pagination Implementation**
- Each page displays 3 articles for optimal readability
- Page numbers are 1-indexed for user-friendliness
- API offset calculation: `(page - 1) * limit`
- Previous button disabled on page 1 to prevent invalid navigation
- Next button always available (API returns empty if no more results)
- Filter changes reset to page 1 for better UX

## Technologies Used

- **Spring Boot 4.0.0:** Application framework
- **Spring Web MVC:** Web layer
- **Thymeleaf:** Server-side templating engine
- **Spring Cache + Caffeine:** Caching abstraction
- **Jackson:** JSON parsing
- **Lombok:** Reduces boilerplate code
- **Maven:** Build tool
- **JUnit 5 (Jupiter):** Unit testing framework
- **Mockito:** Mocking framework for isolated unit testing
- **Spring Test:** Testing support for Spring MVC and MockMvc

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [The News API Documentation](https://www.thenewsapi.com/documentation)
- [Caffeine Caching Library](https://github.com/ben-manes/caffeine)
