# KMDB - Movies API (WIP) 🎬

> **Status**: 🚧 Work In Progress
> **Deadline**: 8.2.2026 23:59
> **Team Size**: 1-2 people

A robust REST API for managing a local film society's movie database. Built with **Spring Boot** and **JPA**, this API replaces legacy spreadsheets with a modern, queryable system for managing movies, genres, and actors.

## 📋 Project Overview

The local film society needs to digitize their records. This API serves as the backend for their new system, allowing them to:

- Manage entities: **Movies**, **Genres**, and **Actors**.
- Handle complex relationships (e.g., Actors starring in multiple Movies).
- Search and filter content (by Year, Genre, Actor).
- Ensure data integrity with input validation and safe deletion policies.

### Core Tech Stack

- **Framework**: Spring Boot (Web, Data JPA)
- **Database**: SQLite (Relational Persistence)
- **Architecture**: Controller-Service-Repository pattern
- **Testing**: Postman Collections

## 🛠 Functional Requirements

### 1. Data Model

- **Genre**: `id`, `name`
- **Movie**: `id`, `title`, `releaseYear`, `duration`
- **Actor**: `id`, `name`, `birthDate` (ISO 8601)

**Relationships**:

- Many-to-Many: `Movie` <-> `Genre`
- Many-to-Many: `Movie` <-> `Actor`

### 2. REST Endpoints

**Entities** (`/api/{entity}`):

- `POST`: Create new
- `GET`: List all (supports Pagination)
- `GET /{id}`: Retrieve by ID
- `PATCH /{id}`: Partial update
- `DELETE /{id}`: Delete (Safe + Force options)

**Filtering**:

- `GET /api/movies?genre={id}`
- `GET /api/movies?year={yyyy}`
- `GET /api/movies?actor={id}`
- `GET /api/movies/{id}/actors`
- `GET /api/actors?name={string}`

**Search**:

- `GET /api/movies/search?title={query}` (Case-insensitive partial match)

### 3. Business Logic & Safety

- **Validation**: `@NotNull`, `@Size` constraints on entities.
- **Deletions**:
  - *Default*: Prevent deletion if relationships exist (e.g., cannot delete an Actor currently cast in a Movie).
  - *Force Delete*: `DELETE /api/actors/1?force=true` (removes relationships, then deletes).
- **Error Handling**:
  - Global Exception Handler (`@ControllerAdvice`)
  - Custom `ResourceNotFoundException`
  - Standardized HTTP Status codes (201, 204, 400, 404).

## 🚀 Setup & Usage (Planned)

1. Clone repository.
2. Configure `application.properties` for SQLite.
3. Run with `./mvnw spring-boot:run`.
4. Import Postman Collection to test.

## 📝 Roadmap

- [ ] Initialize Spring Boot project with Data JPA & Web.
- [ ] Configure SQLite dialect and connection.
- [ ] Implement Entities and Repositories.
- [ ] Build Service Layer with CRUD logic.
- [ ] Develop REST Controllers and Filters.
- [ ] Implement Force Delete logic.
- [ ] Add Pagination & Search.

## 📂 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Buidling REST Services](https://spring.io/guides/gs/rest-service/)

## Contributors 👥

This might be a group project developed by:

- **[Lauri Lepik](https://github.com/LauriLepik)**
- ???

---
*Developed for the **[kood/Nooremarendaja](https://kood.tech/kood-nooremarendaja/)** curriculum (Free, **[NextGenEU](https://kood.tech/meist/toetused/)** funded, 5-month intensive).*
