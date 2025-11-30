# Remote User Fetcher

A Spring Boot application that fetches user data from external APIs, maps fields according to configurable mappings, and stores the results in a database.

## Features

- **Configurable API Endpoints**: Define external APIs with URLs, headers, HTTP methods, and JSON paths
- **Flexible Field Mapping**: Map JSON response fields to database fields using JSONPath expressions
- **Reactive Programming**: Built with Spring WebFlux for non-blocking I/O operations
- **In-Memory Database**: Uses H2 database for easy development and testing
- **RESTful API**: Simple endpoints to trigger data fetching operations

## Technology Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring WebFlux** (Reactive Web)
- **Spring Data JPA**
- **H2 Database** (In-memory)
- **WebClient** for HTTP requests
- **Jayway JsonPath** for JSON parsing
- **Maven**

## Project Structure

```
src/main/java/com/example/remoteuserfetcher/
├── RemoteUserFetcherApplication.java  # Main application class
├── controller/
│   └── FetchController.java           # REST controller for fetch operations
├── service/
│   └── ExternalFetchService.java      # Core service for fetching and processing data
├── entity/
│   ├── ExternalEndpoint.java          # API endpoint configuration
│   ├── FieldMapping.java              # Field mapping definitions
│   └── TemporaryUser.java             # Stored user data
└── repository/
    ├── ExternalEndpointRepository.java
    ├── FieldMappingRepository.java
    └── TemporaryUserRepository.java
```

## Database Schema

### ExternalEndpoint
Stores configuration for external APIs:
- `id`: Primary key
- `name`: Unique identifier for the endpoint
- `url`: API endpoint URL
- `httpMethod`: HTTP method (GET, POST, etc.)
- `headersJson`: JSON string containing request headers
- `listJsonPath`: JSONPath to extract array/list from response

### FieldMapping
Defines how to map JSON fields to user fields:
- `id`: Primary key
- `endpoint`: Reference to ExternalEndpoint
- `targetField`: Target field in TemporaryUser (userId, fullName, email)
- `sourceJsonPath`: JSONPath to extract value from response

### TemporaryUser
Stores processed user data:
- `id`: Primary key
- `userId`, `fullName`, `email`: Mapped user fields
- `rawJson`: Original JSON response
- `source`: Source endpoint name

## API Usage

### Fetch Data from External API

```http
POST /api/fetch/{endpointName}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/fetch/calendly
```

**Response:**
```json
[
  {
    "id": 1,
    "userId": "nandwanaojaswini",
    "fullName": "Ojaswini Nandwana",
    "email": "nandwanaojaswini@gmail.com",
    "rawJson": "{\"avatar_url\":null,\"created_at\":\"2025-03-04T01:40:10.542624Z\",...}",
    "source": "calendly"
  }
]
```

## Configuration

### Application Properties
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:demo
  jpa:
    hibernate.ddl-auto: update
  h2:
    console.enabled: true
```

### Sample Data Setup
The application comes pre-configured with a Calendly API example:

```sql
INSERT INTO EXTERNAL_ENDPOINT (id, name, url, http_method, headers_json, list_json_path)
VALUES (1, 'calendly', 'https://api.calendly.com/users/me', 'GET', '{"Authorization":"Bearer ..."}', '$.resource');

INSERT INTO FIELD_MAPPING (id, endpoint_id, target_field, source_json_path)
VALUES 
  (1, 1, 'userId', '$.slug'),
  (2, 1, 'fullName', '$.name'),
  (3, 1, 'email', '$.email');
```

## Adding New API Integrations

### Step 1: Add Endpoint Configuration
Insert a new record in `EXTERNAL_ENDPOINT` table:

```sql
INSERT INTO EXTERNAL_ENDPOINT (id, name, url, http_method, headers_json, list_json_path)
VALUES (2, 'new-api', 'https://api.example.com/users', 'GET', '{"Authorization":"Bearer token"}', '$.data.users');
```

### Step 2: Define Field Mappings
Map JSON fields to user fields:

```sql
INSERT INTO FIELD_MAPPING (id, endpoint_id, target_field, source_json_path)
VALUES 
  (4, 2, 'userId', '$.id'),
  (5, 2, 'fullName', '$.profile.name'),
  (6, 2, 'email', '$.contact.email');
```

### Step 3: Test the Integration
```bash
curl -X POST http://localhost:8080/api/fetch/new-api
```

## JSONPath Examples

The application uses Jayway JsonPath for extracting data:

- `$` - Root object
- `$.resource` - Nested object under "resource"
- `$.users[*]` - All items in users array
- `$.name` - Direct property access
- `$.profile.email` - Nested property access

## Development

### Running the Application
```bash
mvn spring-boot:run
```

### Access H2 Console
```
http://localhost:8080/h2-console
```
JDBC URL: `jdbc:h2:mem:demo`
Username: `sa`
Password: (empty)

## Error Handling

- **Unknown Endpoint**: Returns 400 Bad Request
- **API Connection Issues**: Returns empty result set
- **JSON Parsing Errors**: Continues with available data
- **Invalid Mappings**: Skips unmappable fields

## Customization

### Adding New User Fields
1. Add field to `TemporaryUser` entity
2. Update field setters in `ExternalFetchService.convertItemToUser()`
3. Add corresponding mappings in database

### Supporting Different Response Formats
Modify `extractItems()` method in `ExternalFetchService` to handle different JSON structures.

## Dependencies

Key dependencies include:
- `spring-boot-starter-webflux`
- `spring-boot-starter-data-jpa`
- `h2`
- `jackson-databind`
- `json-path`

## License

This project is for demonstration purposes.
