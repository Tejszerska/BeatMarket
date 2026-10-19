# BeatMarket

BeatMarket is a B2B music licensing platform exposed as a REST API. It is built as a modular monolith: business
capabilities are separated into modules, while the application currently focuses on the account and music catalog
functionality.

## Modules and implementation status

- **Account** — registration and authentication, including email verification, Google OAuth2, user roles, and access
  control. The functionality is implemented; integration-test coverage still needs to be completed, including the
  user-repository area.
- **Catalog (WIP)** — songs, artists, albums, and genres; catalog search and filtering; audio and image validation and
  processing; and file storage through Amazon S3. The module is nearly complete, with tests still to be finished.
- **Licensing (WIP)** — license tiers, song pricing, and license records. The target flow provisions a license after
  successful payment and delivers its certificate by email.
- **Payment (WIP)** — payment records and statuses. The planned checkout and payment confirmation flow integrates with
  Stripe, including signed webhooks.

The project uses a pragmatic combination of hexagonal and layered architecture. Business modules communicate through
domain-facing ports; external services and technical integrations are handled in the infrastructure layer.

## Tech stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.5.16, Spring MVC
- **Security:** Spring Security, Google OAuth2, JWT
- **Database:** PostgreSQL, Spring Data JPA / Hibernate, Flyway
- **File storage:** Amazon S3 (AWS SDK)
- **Email:** Spring Mail
- **API documentation:** Springdoc OpenAPI / Swagger UI
- **Testing:** JUnit 5, Mockito, AssertJ, Testcontainers
- **Build:** Maven
- **Local environment:** Docker and Docker Compose

## Running locally

### Prerequisites

- Docker and Docker Compose

### Start the application

1. Clone the repository and move into its directory:

   ```bash
   git clone https://github.com/Tejszerska/BeatMarket.git
   cd BeatMarket
   ```

2. Create a local `.env` file from the example:

   ```bash
   cp .env.example .env
   ```

3. Add your Google OAuth2 and Gmail app-password credentials to `.env` if you want to use Google login and email
   verification. Add `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` if you want to use S3 uploads. These variables are passed to
   the application by Docker Compose.

4. Build and start the application and PostgreSQL database:

   ```bash
   docker compose up --build
   ```

The API is available at <https://localhost:8443>, and Swagger UI at <https://localhost:8443/swagger-ui/index.html>. The
local HTTPS certificate may require accepting a browser security warning.

To stop the containers, press `Ctrl+C`; to remove the containers and network afterward, run:

```bash
docker compose down
```

## Documentation

- [Requirements and roadmap](requirements.md)
- [System architecture and flows](architecture.md)
- [API contracts](api-contracts.md)

<img src="/docs/images/swagger-ui.png" alt="Screenshot of Swagger interface for BeatMarket API">