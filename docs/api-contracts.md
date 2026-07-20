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

#### GET /api/users/confirm
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

#### POST /api/users/login
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

#### POST /api/users/logout
Clears the JWT authorization cookie, effectively logging the user out.

**Response (200 OK):**
*Clears the `HttpOnly` cookie in the response headers.*
```json
{
  "message": "Logged out successfully."
}
```
---

#### Browser Flow: Google OAuth2 Login
Initiates the OAuth2 authentication flow via Google. This is not a standard REST endpoint and must be accessed directly via a web browser.

*   **URL:** `GET /oauth2/authorization/google`
*   **Action:** Redirects the user to the Google consent screen. Upon successful authentication, Google redirects back to the backend, which automatically creates the user (if new), issues a JWT cookie, and redirects the user to the Swagger UI/frontend.

### Catalog Module (`catalog`)

---

### User related

#### GET /api/catalog/songs
Fetches a chunked list of all songs

**Request Body Fields:**
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

### Song

#### POST /api/catalog/songs
Adds a new song to the system.

**Request Body Fields:**
*   `title` (string, required) *Title of the song.*
*   `releaseDate` (string, required) *Release date in YYYY-MM-DD format.*
*   `duration` (integer, required) *Duration of the song in seconds. Must be > 0.*
*   `language` (string, required) *Language of the song (e.g., "EN").*
*   `genreId` (integer, optional) *ID of the genre. Can be omitted if the genre is not yet in the system.*
*   `artistIds` (array[integer], optional) *List of artist IDs. Use an empty array `[]` if no artists are assigned yet.*
*   `albumId` (integer, optional) *ID of the album. Can be omitted if the song is not part of an album or it's not yet in the system.*

**Request Body Example:**
```json
{
  "title": "In the End",
  "releaseDate": "2000-10-24",
  "duration": 216,
  "language": "EN",
  "genreId": 1,
  "artistIds": [1],
  "albumId": 2
}
```


**Response (201 Created):**
```json
{
  "id": 10,
  "title": "In the End"
}
```

**Response (400 Bad Request):**
*Invalid input data (e.g., negative duration).*
```json
{
  "status": 400,
  "message": "Duration must be more than 0"
}
```
---
#### POST /api/catalog/songs/{id}/preview
Uploads an audio preview file and links the resulting resource URL to the specified song. If a preview is already linked, the existing file is permanently deleted from the server and the URL is overwritten with the new one.

**Parameters:**
*   `id` (integer, path parameter, required) *Song ID*

**Request Headers:**
*   `Content-Type: multipart/form-data`

**Request Body (Form-Data):**
*   `file` (file/binary, required) *The audio file (e.g., MP3, AAC (.m4a)) to be uploaded.*

**Response (200 OK):**
*Returns the URL of the uploaded resource.*
```json
{
  "message": "Preview uploaded successfully",
  "previewUrl": "https://s3.aws.com/your-bucket/previews/in-the-end-prv.mp3"
}
```
**Error Response (404 Not Found)**
*Returned when the song ID does not exist in the database.*
```json
{
  "status": 404,
  "message": "Song by id=10 was not found."
}
```
**Error Response (400 Bad Request):**
*Returned when the file is missing, empty, or of an unsupported format.*
```json
{
  "status": 400,
  "message": "Invalid file format. Only audio/mpeg (MP3) and audio/aac (AAC) are supported."
}
```
---
#### POST /api/catalog/songs/{id}/track
Uploads the full-length audio track and links the resource URL to the specified song. If a full file is already linked, the existing file is permanently deleted from the server and the URL is overwritten with the new one.
**Parameters:**
*   `id` (integer, path parameter, required) *Song ID*

**Request Headers:**
*   `Content-Type: multipart/form-data`

**Request Body (Form-Data):**
*   `file` (file/binary, required) *High-quality audio file .WAV.*

**Response (200 OK):**
*Returns the URL of the uploaded resource.*
```json
{
  "message": "Full length track uploaded successfully",
  "previewUrl": "https://s3.aws.com/your-bucket/tracks/in-the-end.wav"
}
```
**Error Response (404 Not Found)**
*Returned when the song ID does not exist in the database.*
```json
{
  "status": 404,
  "message": "Song by id=10 was not found."
}
```
**Error Response (400 Bad Request):**
*Returned when the file is missing, empty, too big or of an unsupported format.*
```json
{
  "status": 400,
  "message": "Invalid file format. Only audio/wav (WAV) is supported for full tracks."
}
```
---

#### PATCH /api/catalog/songs/{id}
Partially updates an existing song's metadata and its relationships. All fields are optional. Only the fields provided in the request body will be modified.
**Note:** To explicitly remove an assignment (e.g., detach from an album), send `null` for that specific field.

**Parameters:**
*   `id` (integer, path parameter, required) *Song ID*

**Request Body Example:**
```json
{
  "title": "Corrected Title",
  "duration": 215,
  "releaseDate": "2026-07-18",
  "language": "EN",
  "genreId": 2,
  "albumId": null,
  "artistIds": [1, 45]
}
```


**Response (200 OK):** *Song successfully updated. Returns the updated resource.*
```json
{
  "id": 10,
  "title": "Corrected Title",
  "duration": 215,
  "releaseDate": "2026-07-18",
  "language": "EN",
  "genre": {
    "id": 2,
    "name": "Nu Metal"
  },
  "album": null,
  "artists": [
    {
      "id": 1,
      "name": "Kety"
    },
    {
      "id": 45,
      "name": "New Artist"
    }
  ]
}
```

**Response (404 Not Found):**
*Song, Genre, Album, or Artist(s) not found.*
```json
{
  "status": 404,
  "message": "Artist by id=45 not found."
}
```

**Response (400 Bad Request):**
*Invalid input data (e.g., negative duration).*
```json
{
  "status": 400,
  "message": "Duration must be more than 0"
}
```
---

#### DELETE /api/catalog/songs/{id}
Removes a song from the database by its ID.

**Parameters:**
*   `id` (integer, path parameter, required) *Song ID*

**Response (204 No Content):** *Song deleted successfully. No response body is returned.*

**Response (404 Not Found):** *Song not found*

```json
{
  "status": 404,
  "message": "Song by id=5 not found."
}
```

---

### Album

#### POST /api/catalog/albums
Adds a new album to the system.

**Request Body Fields:**
*   `title` (string, required) *Title of the album.*
*   `releaseDate` (string, required) *Release date in YYYY-MM-DD format.*
*   `songIds` (array[integer], optional) *List of song IDs. Use an empty array `[]` if no songs are assigned yet.*
*   `artistIds` (array[integer], optional) *List of artist IDs. Use an empty array `[]` if no artists are assigned yet.*

**Request Body Example:**
```json
{
  "title": "Cee Dee",
  "releaseDate": "2010-10-10",
  "songIds": [1, 2],
  "artistIds": [1]
}
```


**Response (201 Created):**
```json
{
  "id": 10,
  "title": "Cee Dee"
}
```

**Response (400 Bad Request):**
*Invalid input data (e.g., improper release date format).*
```json
{
  "status": 400,
  "message": "Release date must be in YYYY-MM-DD format"
}
```
---
#### POST /api/catalog/albums/{id}/cover
Uploads album cover (image file) and links the resulting resource URL to the specified album. If a cover is already linked, the existing file is permanently deleted from the server and the URL is overwritten with the new one.

**Parameters:**
*   `id` (integer, path parameter, required) *Album ID*

**Request Headers:**
*   `Content-Type: multipart/form-data`

**Request Body (Form-Data):**
*   `file` (file/binary, required) *The image file (e.g. JPG, PNG) to be uploaded.*

**Response (200 OK):**
*Returns the URL of the uploaded resource.*
```json
{
  "message": "Cover uploaded successfully",
  "previewUrl": "https://s3.aws.com/your-bucket/covers/cee-dee.png"
}
```
**Error Response (404 Not Found)**
*Returned when the album ID does not exist in the database.*
```json
{
  "status": 404,
  "message": "Album by id=10 was not found."
}
```
**Error Response (400 Bad Request):**
*Returned when the file is missing, empty, or of an unsupported format.*
```json
{
  "status": 400,
  "message": "Invalid file format. Only image/jpeg (JPG) and image/png (PNG) are supported."
}
```

---

#### PATCH /api/catalog/albums/{id}
Partially updates an existing album's metadata and its relationships. All fields are optional. Only the fields provided in the request body will be modified.
**Note:** To explicitly remove an assignment (e.g., detach from a song list), send `null` for that specific field.

**Parameters:**
*   `id` (integer, path parameter, required) *Album ID*

**Request Body Example:**
```json
{
  "releaseDate": "2026-07-18",
  "artistIds": [1, 45]
}
```


**Response (200 OK):** *Album successfully updated. Returns the updated resource.*
```json
{
  "id": 10,
  "title": "Dee Dee",
  "releaseDate": "2010-10-10",
  "songs": [
    {
      "id": 2,
      "name": "Bee Gee"
    },
    {
      "id": 4,
      "name": "Pee Gee"
    }
  ],
  "artists": [
    {
      "id": 1,
      "name": "Kety"
    }
    ]
}
```

**Response (404 Not Found):**
*Song, Genre, Album, or Artist(s) not found.*
```json
{
  "status": 404,
  "message": "Album by id=45 not found."
}
```
**Response (400 Bad Request):**
*Invalid input data (e.g., improper release date format).*
```json
{
  "status": 400,
  "message": "Release date must be in YYYY-MM-DD format"
}
```
---

#### DELETE /api/catalog/albums/{id}
Removes an album from the database by its ID.

**Parameters:**
*   `id` (integer, path parameter, required) *Album ID*

**Response (204 No Content):** *Album deleted successfully. No response body is returned.*

**Response (404 Not Found):** *Album not found*

```json
{
  "status": 404,
  "message": "Album by id=5 not found."
}
```
