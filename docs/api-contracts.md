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

### Catalog Module (`catalog`)

---

### User related

#### GET /api/catalog/songs
Fetches a chunked list of all songs

**Parameters:**
*   `page` (integer, query parameter) *Page index; default value: 0*
*   `size` (integer, query parameter) *The size of the page to be returned; default value: 5*
*   `sort` (array[string], query parameter) *Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported; default value: ["id,ASC"]*
*   `genre` (string, query parameter, optional) *Filter by exact genre name.*
*   `artist` (string, query parameter, optional) *Filter by exact artist name.*
*   `language` (string, query parameter, optional) *Filter by track language (e.g., "English", "NONE" for instrumentals).*
*   `album` (string, query parameter, optional) *Filter by exact album name.*
*   `releaseDate` (string, query parameter, optional) *Filter by exact release date in ISO-8601 format (YYYY-MM-DD).*
*   `price` (number, query parameter, optional) *Filter by maximum track price.*
*   `duration` (integer, query parameter, optional) *Filter by maximum track duration in seconds.*

**Response (200 OK):**
```json
{
  "songs": [
    {
      "id": 10,
      "title": "In the End",
      "artist": "U2",
      "genre": "Rock",
      "previewUrl": "https://some.link.com/audio.mp3",
      "album": "Titled Album",
      "language": "English",
      "releaseDate": "2000-10-24",
      "duration": 216,
      "pricing": {
        "Standard": 20.00,
        "Commercial": 50.00,
        "Broadcast": 100.00
      }
    }
  ],
  "hasNext": true
}
```
**Error Response (400 Bad Request):**
*Returned when the client provides invalid query parameters (e.g., negative page index or wrong sort format).*
```json
{
  "status": 400,
  "message": "Invalid sort parameter format. Expected format: property,(asc|desc)"
}
```
---

#### GET /api/catalog/songs/{id}
Retrieves detailed information about a specific song by its ID.

**Parameters:**
*   `id` (integer, path parameter, required) *Song id*

**Response (200 OK):**
```json
{
  "id": 10,
  "title": "In the End",
  "language": "English",
  "releaseDate": "2000-10-24",
  "duration": 216,
  "previewUrl": "https://s3.aws.com/your-bucket/previews/in-the-end-watermark.mp3",
  "artist": {
    "id": 2,
    "name": "U2",
    "imageUrl": "https://s3.aws.com/your-bucket/images/u2-profile.jpg"
  },
  "genre": {
    "id": 1,
    "name": "Rock"
  },
  "album": {
    "id": 7,
    "title": "Something",
    "coverUrl": "https://s3.aws.com/your-bucket/images/something-cover.jpg"
  },
  "pricing": {
    "Standard": 20.00,
    "Commercial": 50.00,
    "Broadcast": 100.00
  }
}
```

**Error Response (404 Not Found):**
*Song with the provided ID does not exist.*
```json
{
  "status": 404,
  "message": "Song by id=7 was not found "
}
```
---

#### GET /api/catalog/genres
Retrieves a chunked list of available music genres.

**Parameters:**
*   `page` (integer, query parameter) *Page index; default value: 0*
*   `size` (integer, query parameter) *The size of the page to be returned; default value: 20*
*   `sort` (array[string], query parameter) *Sorting criteria in the format: property,(asc|desc); default value: ["name,ASC"]*

**Response (200 OK):**
```json
{
  "genres": [
    {
      "id": 1,
      "name": "Rock"
    },
    {
      "id": 2,
      "name": "Cinematic"
    }
  ],
  "hasNext": false
}
```

---

#### GET /api/catalog/artists
Retrieves a chunked list of available artists.

**Parameters:**
*   `page` (integer, query parameter) *Page index; default value: 0*
*   `size` (integer, query parameter) *The size of the page to be returned; default value: 10*
*   `sort` (array[string], query parameter) *Sorting criteria in the format: property,(asc|desc); default value: ["name,ASC"]*
*   `name` (string, query parameter, optional) *Filter by partial or exact artist name.*

**Response (200 OK):**
```json
{
  "artists": [
    {
      "id": 2,
      "name": "U2",
      "imageUrl": "https://s3.aws.com/your-bucket/images/u2-profile.jpg"
    },
    {
      "id": 5,
      "name": "Lady Gaga",
      "imageUrl": "https://s3.aws.com/your-bucket/images/ladygaga-profile.jpg"
    }
  ],
  "hasNext": true
}
```

---

#### GET /api/catalog/albums
Retrieves a chunked list of available albums.

**Parameters:**
*   `page` (integer, query parameter) *Page index; default value: 0*
*   `size` (integer, query parameter) *The size of the page to be returned; default value: 10*
*   `sort` (array[string], query parameter) *Sorting criteria in the format: property,(asc|desc); default value: ["releaseDate,DESC"]*
*   `title` (string, query parameter, optional) *Filter by partial or exact album title.*
*   `artistId` (integer, query parameter, optional) *Filter albums by a specific artist.*

**Response (200 OK):**
```json
{
  "albums": [
    {
      "id": 7,
      "title": "Something",
      "releaseDate": "2000-10-24",
      "coverUrl": "https://s3.aws.com/your-bucket/images/something-cover.jpg",
      "artist": {
        "id": 2,
        "name": "U2"
      }
    },
    {
      "id": 12,
      "title": "Circus",
      "releaseDate": "2014-11-17",
      "coverUrl": "https://s3.aws.com/your-bucket/images/interstellar-cover.jpg",
      "artist": {
        "id": 5,
        "name": "Lady Gaga"
      }
    }
  ],
  "hasNext": true
}
```
---
---

### Admin related:
