## Architecture: modular monolith

### Domains:

| Module | Responsibility | Key Components | Role |
| :--- | :--- | :--- | :--- |
| **1. Catalog Module (`catalog`)** | Manages the core digital assets and inventory. | Songs, Albums, Artists, Genres, and Pricing structures (`song_prices`). | Provides browsing, searching, and administrative CRUD operations for the music database. |
| **2. Account Module (`account`)** | Handles user identity, security, and access control. | Users, Roles, JWT Authentication, OAuth2 Integration, and Email Verification. | Secures the application endpoints and manages user registration and authentication flows. |
| **3. Payment Module (`payment`)** | Manages financial transactions and the checkout process. | Payments history and External Payment Gateway Integration (Stripe Webhooks). | Acts as an intermediary between the application and Stripe to track transaction statuses (e.g., pending, completed, refunded) and ensure payment integrity. |
| **4. Licensing Module (`licensing`)** | Manages digital rights and product provisioning. | Licenses, License Tiers, Validity Periods, and Certificate Issuance. | Grants appropriate usage rights (Standard, Commercial, Broadcast) to users upon successful payment and handles the automated delivery of digital certificates via email. |

### Happy Path (API & Swagger UI Flow)

**1. User authentication** <br>
The user authenticates via the `/api/auth/login` endpoint in Swagger UI and receives an `HttpOnly` cookie containing the JWT token.

**2. Data retrieval** <br>
The user executes a GET request on the `/api/songs` endpoint via Swagger to fetch a list of available songs from the "rock" genre.

**3. Data processing** <br>
The backend queries the database using a JOIN operation with the `song_prices` table and returns the results sorted in ascending order for the `STANDARD` license tier.

**4. Checkout initialization** <br>
The user selects a song ID and executes a POST request on the `/api/payments/checkout` endpoint via Swagger, specifying the desired license tier.

**5. Stripe Session Creation (Internal step)** <br>
The Spring Boot application communicates with the Stripe API to create a Checkout Session. It saves the initial payment status as `PENDING` in the database and returns a JSON response containing the generated Stripe Checkout URL.

**6. Payment execution** <br>
The user manually copies the URL from the Swagger response, opens it in a new browser tab, and completes the test payment via the external Stripe gateway.

**7. Client redirection** <br>
Upon success, Stripe redirects the browser to a simple backend confirmation endpoint (e.g., `GET /api/payments/success`), which returns a basic 200 OK response.

**8. Webhook confirmation (Asynchronous step)** <br>
Stripe servers (or local Stripe CLI) asynchronously hit the hidden `/api/payments/webhook` endpoint with the `payment_intent.succeeded` event payload.

**9. License provisioning** <br>
The `payment` module validates the webhook signature, updates the payment status to `COMPLETED` in the database, and triggers an event in the `licensing` module.

**10. Delivery** <br>
The `licensing` module automatically generates a new record in the `licenses` table and triggers the email service to deliver the digital certificate to the user's registered email address.