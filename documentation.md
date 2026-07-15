### Architecture: modular monolith

### Requirements:

### Phase I: Minimum Viable Product (MVP) - B2B Licensing Platform

#### Account Module
- [ ] The authentication flow incorporates OAuth2 Google login as well as traditional manual registration requiring a password and a mandatory email verification loop.
- [ ] All newly registered users are automatically assigned a customer role.
- [ ] An admin role is provisioned manually within the database to grant secure access for catalog management.

#### Catalog Module
- [ ] Unauthenticated users can search the system using filtering options for the artist, genre, album, language, release date, price and duration, alongside specific sorting capabilities.
- [ ] These public queries return a lightweight data transfer object containing basic metadata and an AWS S3 URL linking to a 30-second watermarked audio sample.
- [ ] The language filtering mechanism supports a dedicated value to strictly identify instrumental tracks.
- [ ] Unauthenticated users can also retrieve general lists of available genres, artists, and albums.
- [ ] Authenticated customers querying a specific song identifier receive a highly detailed response that includes all metadata, associated license tiers, and their respective pricing structures.
- [ ] To manage this data, the admin holds exclusive access to the creation, modification, and deletion endpoints for the entire catalog.
- [ ] The admin is additionally responsible for uploading audio files, which the backend routes directly to an Amazon S3 bucket while persisting the generated URLs in the PostgreSQL database.

#### Payment Module
- [ ] Authenticated users initiate their purchases by sending a request to a dedicated checkout endpoint.
- [ ] The backend then communicates with the Stripe API to create a Checkout Session, logs the initial transaction as pending, and returns the generated Stripe URL.
- [ ] The user executes the test payment on the external Stripe gateway and is automatically redirected to a static backend confirmation endpoint upon completion.
- [ ] Concurrently, a secure webhook endpoint asynchronously receives the success event payload from Stripe servers, validates the cryptographic signature, and updates the payment status in the database to completed.

#### Licensing Module
- [ ] The license provisioning process is triggered entirely internally the moment a payment status shifts to completed.
- [ ] The system automatically generates a new record in the licenses table to legally bind the user to the acquired rights.
- [ ] Immediately following this database update, the integrated email service dispatches a message containing the digital license certificate to the user's registered email address.

#### Infrastructure
- [ ] The entire application is packed into Docker containers and designed for deployment on the AWS cloud.
- [ ] The system inherently relies on Amazon S3 integration to handle all audio file hosting and streaming.

---

### Phase II: Creator Economy (WIP)

#### User Roles & Access
- [ ] The system expands to introduce a dedicated role for content creators.
- [ ] These creators gain the authorization to upload their own songs, set custom pricing for various license tiers, and fully manage their proprietary catalog.
- [ ] The backend enforces resource-level security to guarantee that creators can only modify tracks associated with their specific internal identifier.

---

### Phase III: Web Interface (WIP)

#### Frontend Implementation
- [ ] The final phase introduces a dedicated React single-page application for the frontend.
- [ ] This graphical user interface is built to seamlessly consume the underlying REST API, completely replacing the initial Swagger UI testing flow and providing a full experience for both customers and creators.

### Domains:

| Module | Responsibility | Key Components | Role |
| :--- | :--- | :--- | :--- |
| **1. Catalog Module (`catalog`)** | Manages the core digital assets and inventory. | Songs, Albums, Artists, Genres, and Pricing structures (`song_prices`). | Provides browsing, searching, and administrative CRUD operations for the music database. |
| **2. Account Module (`account`)** | Handles user identity, security, and access control. | Users, Roles, JWT Authentication, OAuth2 Integration, and Email Verification. | Secures the application endpoints and manages user registration and authentication flows. |
| **3. Payment Module (`payment`)** | Manages financial transactions and the checkout process. | Payments history and External Payment Gateway Integration (Stripe Webhooks). | Acts as an intermediary between the application and Stripe to track transaction statuses (e.g., pending, completed, refunded) and ensure payment integrity. |
| **4. Licensing Module (`licensing`)** | Manages digital rights and product provisioning. | Licenses, License Tiers, Validity Periods, and Certificate Issuance. | Grants appropriate usage rights (Standard, Commercial, Broadcast) to users upon successful payment and handles the automated delivery of digital certificates via email. |

###  Happy Path (Phase I)

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