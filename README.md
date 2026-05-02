# 🎵 Songify  🎵
### <i> Advanced Music Database REST API </i>

##  About The Project
Songify is a scalable RESTful API designed for managing a music database (Songs, Albums, Artists, and Genres).

"The core CRUD operations are implemented with a strong emphasis on Clean Code principles, SOLID architecture, and REST API best practices, ensuring high maintainability and readability.
<br>**Furthermore, the primary focus of this project is a highly secure Hybrid Authentication System**. It combines traditional manual registration with Google OAuth2 Login, unified under a custom RSA-signed JWT architecture.

## Tech Stack
* **Language:** Java 17
* **Framework:** Spring Boot 3
* **Security:** Spring Security 6, OAuth2 Client, Auth0 JWT
* **Database:** PostgreSQL, Spring Data JPA, Hibernate
* **Testing:** JUnit 5, Mockito, Testcontainers
* **Documentation:** Springdoc OpenAPI (Swagger)
* **Build Tool:** Maven
* **DevOps:** Docker, Docker Compose

## Key Features
* **Advanced Hybrid Security Architecture:**
    * **Unified Token System:** Both traditional login and Google OAuth2 result in a custom, self-signed RSA-256 JWT.
    * **Stateless Sessions:** Fully stateless architecture utilizing `HttpOnly` and `Secure` cookies to prevent XSS attacks. No tokens are exposed in the Local Storage.
    * **Auto-Registration:** Users logging in via Google for the first time are automatically registered in the database.
    * **Email Verification:** Manual registration requires account activation via an email confirmation link.
* **Complex Data Relations:** Managed Many-to-Many and One-to-Many relationships between Artists, Albums, and Songs using Hibernate/JPA.
* **Interactive API Documentation:** Beautifully integrated Swagger UI / OpenAPI 3.0.
* **Fully Containerized:** The entire application, including the PostgreSQL database, is fully containerized using a multi-stage Docker build and Docker Compose.

###  Security Flow Highlights
As a junior developer, I wanted to go beyond standard Basic Auth. Here is how the security flows in this application:
1. **OAuth2 Flow:** When a user logs in via Google, the application intercepts the Google Token, verifies the user, and discards the Google Token.
2. **Custom JWT Generation:** The backend then generates its own custom JWT signed with a private RSA key.
3. **Cookie Delivery:** This JWT is embedded into an `HttpOnly`, `Secure` cookie and sent to the browser.
4. **Custom Filter:** Every subsequent request is intercepted by a custom `JwtAuthTokenFilter` that extracts the cookie, validates the RSA signature using the public key, and sets the Spring `SecurityContext`.

## Getting Started

### Prerequisites
* Docker and Docker Compose installed on your machine.
* *(No local Java or Maven installation is required to run the project, as it uses a multi-stage Docker build).*

### Installation & Running
1. Clone the repository:
```bash
   git clone https://github.com/Tejszerska/songify
```
Navigate to the project directory:
```bash
cd songify
```

Build and start the entire ecosystem (App + Database) using Docker Compose:
```bash
docker-compose up -d --build
```
### API Documentation
Once the application is running, you can interact with the API and test the authentication flows using the Swagger UI:

URL: https://localhost:8443/swagger-ui/index.html 

### ⚠️ Note on Environment Variables
For security reasons, sensitive data like the **Google OAuth2 Client Secret** and **SMTP Email Password** are not included in this repository.
* The application will still successfully boot up using fallback dummy values.
* Core CRUD operations and traditional manual JWT authentication will work perfectly.
<br><br> (Note: requires accepting the self-signed SSL certificate for HTTPS).

<img src="/images/swagger-ui.png" alt="Screenshot of Swagger interface for Songify API">