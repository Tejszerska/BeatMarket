### API Endpoints / JSON Payloads

### Account Module (`account`)

---

#### POST /api/users/register
Creates an inactive user account and triggers a confirmation email.

**Request Body:**
```json
{
  "email": "test@gmail.com",
  "password": "SuperSecret123!"
}
```
**Response (201 Created):**
```json
{
  "message": "User created successfully. Confirmation email sent."
}
```
**Error Response (400 Bad Request):**
*Returned when validation fails (e.g., weak password, invalid email format).*
```json
{
  "message": "Invalid registration data."
}
```

**Error Response (409 Conflict):**
*Returned when the email address is already registered in the system.*
```json
{
  "message": "User with this email already exists."
}
```

---

### GET /api/users/confirm
Validates the token from the email link and activates the user account.

**Parameters:**
*   `token` (string, required, query parameter)

**Response (302 Found):**
*Redirects the user to the Swagger UI or frontend application upon successful activation.*
```json
{
"message": "Account activated. Redirecting to Swagger UI."
}
```
**Error Response (404 Not Found):**
*Returned when the token is invalid, expired, or already used.*
```json
{
"message": "Confirmation failed. User cannot login."
}
```
---

### POST /api/users/login
Authenticates a user and issues an HttpOnly JWT cookie for subsequent requests.

**Request Body:**
```json
{
"email": "test@gmail.com",
"password": "SuperSecret123!"
}
```
**Response (200 OK):**
*Sets an `HttpOnly` authorization cookie in the response headers.*
```json
{
"message": "Login successful."
}
```
**Error Response (401 Unauthorized):**
*Returned for incorrect credentials or unactivated accounts.*
```json
{
"message": "Invalid email or password."
}
```
---

### POST /api/users/logout
Clears the JWT authorization cookie, effectively logging the user out.

**Response (200 OK):**
*Clears the `HttpOnly` cookie in the response headers.*
```json
{
"message": "Logged out successfully."
}
```
---

### Browser Flow: Google OAuth2 Login
Initiates the OAuth2 authentication flow via Google. This is not a standard REST endpoint and must be accessed directly via a web browser.

*   **URL:** `GET /oauth2/authorization/google`
*   **Action:** Redirects the user to the Google consent screen. Upon successful authentication, Google redirects back to the backend, which automatically creates the user (if new), issues a JWT cookie, and redirects the user to the Swagger UI/frontend.