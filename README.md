# News Feed Application
News project for FSP Assessment Day

**Author:** Daniel Bee

## Project Overview

This is a Spring Boot web application that displays the top 3 news stories from The News API. It demonstrates clean architecture, SOLID principles, and modern Java development practices.

## Key Features

- **Regional News Filtering:** Users can select from 8 different regions (US, UK, Australia, Canada, India, Ireland, New Zealand, Singapore)
- **Category Filtering:** Users can filter by 7 news categories (General, Business, Entertainment, Health, Science, Sports, Technology)
- **Combined Filtering:** Both region and category filters work simultaneously for precise news discovery
- **Smart Caching:** Region and category-specific caching ensures fast load times while respecting API limits
- **Responsive Design:** Clean, mobile-friendly interface with flexible filter layout
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
- Cache is **combination-specific** using composite keys (locale + category)
- Cache key format: `"us-business"`, `"gb-technology"`, etc.
- Ensures users can switch filters without stale data
- 24-hour cache expiration balances freshness with API conservation
- Graceful degradation: returns empty list on API failure rather than crashing

**Query Parameter Handling**
- Uses `@RequestParam` with default values for clean URL structure
- Supports multiple filter combinations via query parameters
- Example: `/news?locale=gb&category=technology` (UK technology news)
- Both parameters are optional and have sensible defaults (US, General)
- LinkedHashMap maintains consistent filter ordering in dropdowns

## Technologies Used

- **Spring Boot 4.0.0** - Application framework
- **Spring Web MVC** - Web layer
- **Thymeleaf** - Server-side templating engine
- **Spring Cache + Caffeine** - Caching abstraction
- **Jackson** - JSON parsing
- **Lombok** - Reduces boilerplate code
- **Maven** - Build tool

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [The News API Documentation](https://www.thenewsapi.com/documentation)
- [Caffeine Caching Library](https://github.com/ben-manes/caffeine)
