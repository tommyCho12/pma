# Project Management Application (PMA)

A comprehensive web-based application designed to streamline project management, employee tracking, and resource assignment.

## 🚀 Overview

The Project Management Application (PMA) helps organizations manage their projects and workforce effectively. It provides tools for:
- Tracking project timelines and statuses.
- Managing employee details and assignments.
- Visualizing data through interactive charts.
- Handling secure user authentication and authorization.

## 🛠 Technologies Used

### Backend
- **Java 17**: Core programming language.
- **Spring Boot 2.7.18**: Application framework.
    - **Spring Data JPA**: For relational database interaction.
    - **Spring Data MongoDB**: For NoSQL database interaction.
    - **Spring Security**: For authentication and access control.
    - **Spring Boot Actuator**: For monitoring and managing the application.
    - **Thymeleaf**: Server-side Java template engine for the frontend.

### Database
- **PostgreSQL**: Primary relational database.
- **MongoDB**: NoSQL database for specific data requirements.
- **H2 Database**: In-memory database for testing and development.

### Testing
- **JUnit 5**: Unit and integration testing.
- **Playwright (TypeScript)**: End-to-end (E2E) testing located in the `e2e/` directory.
- **REST Assured**: For testing REST services.

### DevOps & Tools
- **Maven**: Build and dependency management.
- **Docker & Docker Compose**: Containerization and orchestration of services (App, PostgreSQL, MongoDB).
- **GitHub Actions**: CI/CD pipelines (configured in `.github/workflows`).

## ⚙️ Prerequisites

- **Java 17** SDK
- **Maven 3.8+**
- **Docker** and **Docker Compose**
- **Node.js** (for running E2E tests locally)

## 🏃‍♂️ Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/<your-username>/pma.git
cd pma
```

### 2. Run with Docker (Recommended)
You can run the databases and the application using Docker Compose.
```bash
docker-compose -f docker-compose-mongodb.yml up -d
```
*Note: Ensure the `Dockerfile` and compose configurations are set up to build the app image if not using the pre-built `mongo` and `postgres` images only.*

### 3. Run Locally (Manual)

**Start Databases:**
If you prefer running the app locally with Maven, verify your database instances (PostgreSQL/MongoDB) are running on ports `5432` and `27017` respectively.

**Build and Run:**
```bash
mvn clean install
mvn spring-boot:run
```

Access the application at: `http://localhost:8080`

## 🧪 Running Tests

### Unit & Integration Tests
```bash
mvn test
```

### End-to-End (E2E) Tests
Navigate to the `e2e` directory and install dependencies:
```bash
cd e2e
npm install
npx playwright test
```

## 📂 Project Structure
- `src/main/java`: Backend source code (Controllers, Entities, Services).
- `src/main/resources`: Configuration (`application.properties`) and Frontend templates (`templates/`).
- `e2e/`: Playwright TypeScript E2E test suite.
- `.github/workflows`: CI/CD pipeline configurations(github actions).
- `docker-compose-mongodb.yml`: Docker Compose configuration for services.

## 📧 Contact
For any questions or issues, please contact [tomasnt4@gmail.com].
