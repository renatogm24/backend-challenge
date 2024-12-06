
# Clara API - Artist Management

## Menu
1. Overview
2. Project Requirements
3. Endpoints
    - Search Artists
    - Fetch Artist Details
    - Compare Artists
4. Architecture
5. Running the Application
    - Using Docker Compose (Recommended)
    - Running Locally with Maven
6. Testing
    - Unit Tests
7. Code Quality
8. Postman Collection
9. Important Notes

---

# 1. Overview
Clara API is a service designed to search for and manage artist information, including their discography, by consuming the [Discogs API](https://www.discogs.com/). The service implements a hexagonal architecture to promote modularity, reusability, and maintainability. It interacts with a PostgreSQL database for persistence and incorporates resilience mechanisms to handle external API limitations, such as strict rate limiting.

### Features
1. **Search Artists**: Endpoint to search artists by name.
2. **Fetch Artist Details**: Endpoint to retrieve detailed information about an artist, including their releases.
3. **Compare Artists**: Endpoint to compare two artists based on their discography.

---

# 2. Project Requirements
- **Java**: 21
- **Maven**: 3.x
- **Spring Boot**: 3.4.0
- **PostgreSQL**: 13.1 (or compatible)
- **Docker**: For containerization (optional).

---
# 3. Endpoints

## Search Artists
**GET** `/api/artists/search?name={artistName}`
- **Description**: Searches for artists by name.
- **Query Parameter**:
    - `name` (required): The name of the artist to search for.
- **Example**:  
  Request:
  ```bash
  GET /api/artists/search?name=Bruno%20Mars
  ```


## Fetch Artist Details
**POST** `/api/artists/{artistId}`
- **Description**: Retrieves detailed information about an artist and optionally fetches their releases from the Discogs API.
- **Path Parameter**:
    - `artistId` (required): The ID of the artist.
- **Request Body**:
  ```json
  {
    "fetchAll": true,
    "force": true,
    "limit": 50,
    "page": 0,
    "size": 5
  }
  ```
    - **Parameters**:
        - `fetchAll`: When `true`, fetches all releases for the artist. Requires an API key without strict rate limits. Default is `false`.
        - `force`: When `true`, forces the endpoint to fetch data from the Discogs API even if the artist already exists in the database. Default is `false`.
        - `limit`: Limits the number of releases fetched if `fetchAll` is `false`. Default is `50`.
        - `page` and `size`: Pagination parameters for the releases. Default `page` is `0`, and `size` is `5`.


## Compare Artists
**POST** `/api/artists/compare`

- **Description**: Compares two artists and their discography.
- **Request Body**:
  ```json
  {
    "artistId1": 819015,
    "artistId2": 123456
  }
  ```


---

# 4. Architecture
The service implements **Hexagonal Architecture** to keep the core business logic decoupled from external dependencies. Key design principles:
- **Core**: Contains domain models and business rules.
- **Application**: Implements use cases like searching and managing artists.
- **Infrastructure**: Handles integration with external services like the Discogs API, database persistence, and configurations.

### Key Technologies
1. **Spring Data JPA with Hibernate**: For database interactions with PostgreSQL.
2. **Discogs API Client**: Handles API calls to fetch artist details and releases, with strict handling of rate limits.
3. **Resilience4j**: Implements Circuit Breaker patterns to manage faults and fallback mechanisms.
4. **Swagger/OpenAPI**: Generates API documentation accessible at `http://localhost:8080/api-docs`.

---

# 5. Running the Application

## Using Docker Compose (Recommended)
1. Make sure Docker is installed and running.
2. Replace the Discogs API token in `application.yml` or set it as an environment variable (`DISCOGS_API_TOKEN`).
3. Run the following command to start the application and PostgreSQL:
   ```bash
   docker-compose up --build
   ```
4. The API will be available at `http://localhost:8080`.

## Running Locally with Maven
1. Install PostgreSQL locally and create a database named `artistdb`.
2. Update `application.yml` with your local PostgreSQL configurations.
3. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```

---

# 6. Testing

## Unit Tests
- Tests are implemented using **JUnit 5** and **Mockito** for services in the `application` layer.
- Coverage is measured using **JaCoCo**, and the results can be found in `resources/reports/jacoco/index.html`.


---

# 7. Code Quality
- The project has been analyzed with **SonarQube** to ensure code quality. Report image is located in `resources/reports`.
- JaCoCo and SonarQube reports indicate code coverage and maintainability metrics.

---

# 8. Postman Collection
- A Postman collection is available in `resources/postman`. Import it to test the endpoints easily.

---

# 9. Important Notes
- Replace the Discogs API token in the configuration file or environment variable to enable API calls.
- Ensure Docker is running if you prefer the `docker-compose` setup.
