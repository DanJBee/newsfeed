# News Feed Application
News project for FSP Assessment Day

**Author:** Daniel Bee

## Project Overview

A Spring Boot web application that displays the top 3 news stories from The News API.

## Features

- **Regional News Filtering:** Select from 8 different regions (US, UK, Australia, Canada, India, Ireland, New Zealand, Singapore)
- **Category Filtering:** Filter by 7 news categories (General, Business, Entertainment, Health, Science, Sports, Technology)
- **Pagination:** Navigate through pages of articles (3 per page)
- **Combined Filtering:** All filters work together
- **Smart Caching:** Caching implemented to avoid hitting API rate limits
- **Responsive Design:** Mobile-friendly interface
- **External Links:** Click through to read full articles

## Technologies Used

- **Spring Boot 4.0.0:** Application framework
- **Spring Web MVC:** Web layer
- **Thymeleaf:** Templating engine for the UI
- **Spring Cache + Caffeine:** Caching to handle API rate limits
- **Jackson:** JSON parsing
- **Lombok:** Reduces boilerplate code
- **Maven:** Build tool
- **JUnit 5 & Mockito:** Unit testing

## Implementation Notes

### Architecture
The application follows a three-layer MVC architecture:
- **Controller:** Handles HTTP requests and passes data to the view
- **Service:** Contains business logic for fetching and processing news
- **Model:** Represents the data structures

### Key Design Decisions

**Caching**
Implemented caching to respect The News API's rate limits. Cache keys combine locale, category, and page number (e.g., "us-business-1") so that different filter combinations don't return stale data. Cache entries expire after 24 hours.

**Pagination**
Shows 3 articles per page as requested. Page numbers are 1-indexed for better UX. The API offset is calculated as `(page - 1) * 3`.

**Error Handling**
If the API call fails, the service returns an empty list instead of crashing, so the user sees a graceful empty state.

**Dependency Injection**
Used constructor injection for better testability - makes it easy to provide mock services during testing.

## Testing

Basic unit tests cover:
- Default parameter handling
- Filter combinations (region, category, pagination)
- API response parsing
- Error handling when the API is unavailable

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [The News API Documentation](https://www.thenewsapi.com/documentation)
- [Caffeine Caching Library](https://github.com/ben-manes/caffeine)
