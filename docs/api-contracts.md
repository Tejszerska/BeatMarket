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
_Returned when validation fails (e.g., weak password, invalid email format)._

```json
{
  "message": "Validation failed",
  "errors": {
    "email": "Invalid email format",
    "password": "Password must be at least 8 characters long"
  }
}
```

**Error Response (409 Conflict):**
_Returned when the email address is already registered in the system._

```json
{
  "message": "User with this email already exists."
}
```

---

#### GET /api/users/confirm

Validates the token from the email link and activates the user account.

**Parameters:**

- `token` (string, required, query parameter)

**Response (302 Found):**
_Redirects the user to the Swagger UI or frontend application upon successful activation._

```json
{
  "message": "Account activated. Redirecting to Swagger UI."
}
```

**Error Response (404 Not Found):**
_Returned when the token is invalid, expired, or already used._

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
_Sets an `HttpOnly` authorization cookie in the response headers._

```json
{
  "message": "Login successful."
}
```

**Error Response (401 Unauthorized):**
_Returned for incorrect credentials or unactivated accounts._

```json
{
  "message": "Invalid email or password."
}
```

---

#### POST /api/users/logout

Clears the JWT authorization cookie, effectively logging the user out.

**Response (200 OK):**
_Clears the `HttpOnly` cookie in the response headers._

```json
{
  "message": "Logged out successfully."
}
```

---

#### Browser Flow: Google OAuth2 Login

Initiates the OAuth2 authentication flow via Google. This is not a standard REST endpoint and must be accessed directly via a web browser.

- **URL:** `GET /oauth2/authorization/google`
- **Action:** Redirects the user to the Google consent screen. Upon successful authentication, Google redirects back to the backend, which automatically creates the user (if new), issues a JWT cookie, and redirects the user to the Swagger UI/frontend.

### Catalog Module (`catalog`)

---

### User related

#### GET /api/catalog/songs

Fetches a chunked list of all songs

**Query Parameters:**

- `page` (integer, query parameter) _Page index; default value: 0_
- `size` (integer, query parameter) _The size of the page to be returned; default value: 5_
- `sort` (array[string], query parameter) _Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported; default value: ["editedOn,ASC"]_
- `genre` (string, query parameter, optional) _Filter by exact genre name._
- `artist` (string, query parameter, optional) _Filter by exact artist name._
- `language` (string, query parameter, optional) _Filter by track language (e.g., "EN", "NONE" for instrumentals)._
- `album` (string, query parameter, optional) _Filter by exact album name._
- `releaseDate` (string, query parameter, optional) _Filter by exact release date in ISO-8601 format (YYYY-MM-DD)._
- `minDuration` (integer, query parameter, optional) _Filter by minimum track duration in seconds._
- `maxDuration` (integer, query parameter, optional) _Filter by maximum track duration in seconds._
- `maxPrice` (number, query parameter, optional) _Filter by maximum track price. **Note**: If provided, `currency` and `license` must also be specified._
- `currency` (string, query parameter, optional) _Filter by currency. Required if `maxPrice` is used._
- `license` (string, query parameter, optional) _Filter by license type. Required if `maxPrice` is used._

**Response (200 OK):**

```json
{
  "songs": [
    {
      "id": 10,
      "title": "In the End",
      "artists": [
        {
          "id": 2,
          "name": "U2"
        },
        {
          "id": 8,
          "name": "Coldplay"
        }
      ],
      "genre": {
        "id": 1,
        "name": "Rock"
      },
      "previewUrl": "https://some.link.com/audio.mp3",
      "album": {
        "id": 7,
        "title": "Something"
      },
      "language": "EN",
      "releaseDate": "2000-10-24",
      "duration": 216,
      "pricing": {
        "Standard": {
          "amount": 20.0,
          "currency": "USD"
        },
        "Commercial": {
          "amount": 50.0,
          "currency": "USD"
        }
      }
    }
  ],
  "hasNext": true
}
```

**Error Response (400 Bad Request):**
_Returned when the client provides invalid query parameters (e.g., negative page index or wrong sort format)._

```json
{
  "message": "Validation failed",
  "errors": {
    "duration": "Duration must be more than 0"
  }
}
```

---

#### GET /api/catalog/songs/{id}

Retrieves detailed information about a specific song by its ID.

**Parameters:**

- `id` (integer, path parameter, required) _Song id_

**Response (200 OK):**

```json
{
  "id": 10,
  "title": "In the End",
  "language": "EN",
  "releaseDate": "2000-10-24",
  "duration": 216,
  "previewUrl": "https://s3.aws.com/your-bucket/previews/in-the-end-watermark.mp3",
  "artists": [
    {
      "id": 2,
      "name": "U2",
      "imageUrl": "https://s3.aws.com/your-bucket/images/u2-profile.jpg",
      "displayOrder": 1
    },
    {
      "id": 8,
      "name": "Coldplay",
      "imageUrl": "https://s3.aws.com/your-bucket/images/coldplay-profile.jpg",
      "displayOrder": 2
    }
  ],
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
    "Standard": {
      "amount": 20.0,
      "currency": "USD"
    },
    "Commercial": {
      "amount": 50.0,
      "currency": "USD"
    }
  }
}
```

**Error Response (404 Not Found):**
_Song with the provided ID does not exist._

```json
{
  "message": "Song by id=7 was not found "
}
```

---

#### GET /api/catalog/genres

Retrieves a chunked list of available music genres.

**Parameters:**

- `page` (integer, query parameter) _Page index; default value: 0_
- `size` (integer, query parameter) _The size of the page to be returned; default value: 20_
- `sort` (array[string], query parameter) _Sorting criteria in the format: property,(asc|desc); default value: ["editedOn,ASC"]_

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

#### GET /api/catalog/genres/{id}

Retrieves the detailsResponse of a specific genre by its ID.

**Parameters:**

- `id` (integer, path parameter, required) _Genre ID_

**Response (200 OK):**

```json
{
  "id": 1,
  "name": "Rock"
}
```

**Error Response (404 Not Found):** Returned when the genre with the provided ID does not exist.

```json
{
  "message": "Genre by id=1 was not found."
}
```

---

#### GET /api/catalog/artists

Retrieves a chunked list of available artists.

**Parameters:**

- `page` (integer, query parameter) _Page index; default value: 0_
- `size` (integer, query parameter) _The size of the page to be returned; default value: 20_
- `sort` (array[string], query parameter) _Sorting criteria in the format: property,(asc|desc); default value: ["editedOn,ASC"]_
- `name` (string, query parameter, optional) _Filter by partial or exact artist name._

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

#### GET /api/catalog/artists/{id}

Retrieves detailed information about a specific artist by their ID, including their albums and songs.

**Parameters:**

- `id` (integer, path parameter, required) _Artist ID_

**Response (200 OK):**

```json
{
  "id": 2,
  "name": "U2",
  "imageUrl": "https://s3.aws.com/your-bucket/images/u2-profile.jpg",
  "albums": [
    {
      "id": 7,
      "title": "Something",
      "releaseDate": "2000-10-24"
    }
  ],
  "songs": [
    {
      "id": 10,
      "title": "In the End",
      "duration": 216
    },
    {
      "id": 11,
      "title": "Crawling",
      "duration": 208
    }
  ]
}
```

**Error Response (404 Not Found):** Returned when the artist with the provided ID does not exist.

```json
{
  "message": "Artist by id=2 was not found."
}
```

---

#### GET /api/catalog/albums

Retrieves a chunked list of available albums.

**Parameters:**

- `page` (integer, query parameter) _Page index; default value: 0_
- `size` (integer, query parameter) _The size of the page to be returned; default value: 10_
- `sort` (array[string], query parameter) _Sorting criteria in the format: property,(asc|desc); default value: ["editedOn,ASC"]_
- `title` (string, query parameter, optional) _Filter by partial or exact album title._
- `artistId` (integer, query parameter, optional) _Filter albums by a specific artist._

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

#### GET /api/catalog/albums/{id}

Retrieves detailed information about a specific album by its ID, including the list of associated songs.
**Parameters:**

- `id` (integer, path parameter, required) _Album ID_

**Response (200 OK):**

```json
{
  "id": 7,
  "title": "Something",
  "releaseDate": "2000-10-24",
  "coverUrl": "https://s3.aws.com/your-bucket/images/something-cover.jpg",
  "artist": {
    "id": 2,
    "name": "U2"
  },
  "songs": [
    {
      "id": 10,
      "title": "In the End",
      "duration": 216
    },
    {
      "id": 11,
      "title": "Crawling",
      "duration": 208
    }
  ]
}
```

**Error Response (404 Not Found):** Returned when the album with the provided ID does not exist.

```json
{
  "message": "Album by id=7 was not found."
}
```

---

---

### Admin related:

### Song

#### POST /api/catalog/songs

Adds a new song to the system.

**Request Body Fields:**

- `title` (string, required) _Title of the song._
- `releaseDate` (string, required) _Release date in YYYY-MM-DD format._
- `duration` (integer, required) _Duration of the song in seconds. Must be > 0._
- `language` (string, required) _Language of the song (e.g., "EN")._
- `genreId` (integer, optional) _ID of the genre. Can be omitted if the genre is not yet in the system._
- `artistIds` (array[integer], optional) _List of artist IDs. Use an empty array `[]` if no artists are assigned yet. The first ID in the array is treated as the primary artist, while subsequent IDs represent featuring artists._
- `albumId` (integer, optional) _ID of the album. Can be omitted if the song is not part of an album or it's not yet in the system._

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
  "title": "In the End",
  "language": "EN",
  "releaseDate": "2000-10-24",
  "duration": 216,
  "artists": [
    {
      "id": 2,
      "name": "U2"
    },
    {
      "id": 8,
      "name": "Coldplay"
    }
  ],
  "genre": {
    "id": 1,
    "name": "Rock"
  },
  "album": {
    "id": 7,
    "title": "Something"
  }
}
```

**Response (400 Bad Request):**
_Invalid input data (e.g., negative duration)._

```json
{
  "message": "Validation failed",
  "errors": {
    "duration": "Duration must be more than 0"
  }
}
```

---

#### POST /api/catalog/songs/{id}/preview

Uploads an audio preview file and links the resulting resource URL to the specified song. If a preview is already linked, the existing file is permanently deleted from the server and the URL is overwritten with the new one.

**Parameters:**

- `id` (integer, path parameter, required) _Song ID_

**Request Headers:**

- `Content-Type: multipart/form-data`

**Request Body (Form-Data):**

- `file` (file/binary, required) _The audio file (e.g., MP3, AAC (.m4a)) to be uploaded._

**Response (200 OK):**
_Returns the URL of the uploaded resource._

```json
{
  "message": "Preview uploaded successfully",
  "previewUrl": "https://s3.aws.com/your-bucket/previews/in-the-end-prv.mp3"
}
```

**Error Response (404 Not Found)**
_Returned when the song ID does not exist in the database._

```json
{
  "message": "Song by id=10 was not found."
}
```

**Error Response (400 Bad Request):**
_Returned when the file is missing, empty, or of an unsupported format._

```json
{
  "message": "Invalid file format. Only audio/mpeg (MP3) and audio/aac (AAC) are supported."
}
```

---

#### POST /api/catalog/songs/{id}/track

Uploads the full-length audio track and links the resource URL to the specified song. If a full file is already linked, the existing file is permanently deleted from the server and the URL is overwritten with the new one.
**Parameters:**

- `id` (integer, path parameter, required) _Song ID_

**Request Headers:**

- `Content-Type: multipart/form-data`

**Request Body (Form-Data):**

- `file` (file/binary, required) _High-quality audio file .WAV._

**Response (200 OK):**
_Returns the URL of the uploaded resource._

```json
{
  "message": "Full length track uploaded successfully",
  "trackUrl": "https://s3.aws.com/your-bucket/tracks/in-the-end.wav"
}
```

**Error Response (404 Not Found)**
_Returned when the song ID does not exist in the database._

```json
{
  "message": "Song by id=10 was not found."
}
```

**Error Response (400 Bad Request):**
_Returned when the file is missing, empty, too big or of an unsupported format._

```json
{
  "message": "Invalid file format. Only audio/wav (WAV) is supported for full tracks."
}
```

---

#### PATCH /api/catalog/songs/{id}

Partially updates an existing song's metadata and its relationships. All fields are optional. Only the fields provided in the request body will be modified.
**Note:** To explicitly remove an assignment (e.g., detach from an album), send `null` for that specific field.

**Parameters:**

- `id` (integer, path parameter, required) _Song ID_

**Request Body Fields:**

- `title` (string, optional) _Title of the song._
- `releaseDate` (string, optional) _Release date in YYYY-MM-DD format._
- `duration` (integer, optional) _Duration of the song in seconds. Must be > 0._
- `language` (string, optional) _Language of the song (e.g., "EN")._
- `genreId` (integer, optional) _ID of the genre. Can be omitted if the genre is not yet in the system._
- `artistIds` (array[integer], optional) _List of artist IDs._
- `albumId` (integer, optional) _ID of the album._

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

**Response (200 OK):** _Song successfully updated. Returns the updated resource._

```json
{
  "id": 10,
  "title": "In the End",
  "language": "EN",
  "releaseDate": "2000-10-24",
  "duration": 216,
  "artists": [
    {
      "id": 2,
      "name": "U2"
    },
    {
      "id": 8,
      "name": "Coldplay"
    }
  ],
  "genre": {
    "id": 1,
    "name": "Rock"
  },
  "album": {
    "id": 7,
    "title": "Something"
  }
}
```

**Response (404 Not Found):**
_Song, Genre, Album, or Artist(s) not found._

```json
{
  "message": "Artist by id=45 not found."
}
```

**Response (400 Bad Request):**
_Invalid input data (e.g., negative duration)._

```json
{
  "message": "Duration must be more than 0"
}
```

---

#### DELETE /api/catalog/songs/{id}

Removes a song from the database by its ID.

**Parameters:**

- `id` (integer, path parameter, required) _Song ID_

**Response (204 No Content):** _Song deleted successfully. No response body is returned._

**Response (404 Not Found):** _Song not found_

```json
{
  "message": "Song by id=5 not found."
}
```

---

### Album

#### POST /api/catalog/albums

Adds a new album to the system.

**Request Body Fields:**

- `title` (string, required) _Title of the album._
- `releaseDate` (string, required) _Release date in YYYY-MM-DD format._
- `songIds` (array[integer], optional) _List of song IDs. Use an empty array `[]` if no songs are assigned yet._
- `artistIds` (array[integer], optional) _List of artist IDs. Use an empty array `[]` if no artists are assigned yet._

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
  "title": "Dee Dee",
  "releaseDate": "2010-10-10",
  "songs": [
    {
      "id": 2,
      "title": "Bee Gee"
    },
    {
      "id": 4,
      "title": "Pee Gee"
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

**Response (400 Bad Request):**
_Invalid input data (e.g., improper release date format)._

```json
{
  "message": "Validation failed",
  "errors": {
    "releaseDate": "Release date must be in YYYY-MM-DD format"
  }
}
```

---

#### POST /api/catalog/albums/{id}/cover

Uploads album cover (image file) and links the resulting resource URL to the specified album. If a cover is already linked, the existing file is permanently deleted from the server and the URL is overwritten with the new one.

**Parameters:**

- `id` (integer, path parameter, required) _Album ID_

**Request Headers:**

- `Content-Type: multipart/form-data`

**Request Body (Form-Data):**

- `file` (file/binary, required) _The image file (e.g. JPG, PNG) to be uploaded._

**Response (200 OK):**
_Returns the URL of the uploaded resource._

```json
{
  "message": "Cover uploaded successfully",
  "coverUrl": "https://s3.aws.com/your-bucket/covers/cee-dee.png"
}
```

**Error Response (404 Not Found)**
_Returned when the album ID does not exist in the database._

```json
{
  "message": "Album by id=10 was not found."
}
```

**Error Response (400 Bad Request):**
_Returned when the file is missing, empty, or of an unsupported format._

```json
{
  "message": "Invalid file format. Only image/jpeg (JPG) and image/png (PNG) are supported."
}
```

---

#### PATCH /api/catalog/albums/{id}

Partially updates an existing album's metadata and its relationships. All fields are optional. Only the fields provided in the request body will be modified.
**Note:** To explicitly remove an assignment (e.g., detach from a song list), send `null` for that specific field.

**Parameters:**

- `id` (integer, path parameter, required) _Album ID_

**Request Body Fields:**

- `title` (string, optional) _Title of the album._
- `releaseDate` (string, optional) _Release date in YYYY-MM-DD format._
- `songIds` (array[integer], optional) _List of song IDs._
- `artistIds` (array[integer], optional) _List of artist IDs._

**Request Body Example:**

```json
{
  "releaseDate": "2026-07-18",
  "artistIds": [1, 45]
}
```

**Response (200 OK):** _Album successfully updated. Returns the updated resource._

```json
{
  "id": 10,
  "title": "Dee Dee",
  "releaseDate": "2010-10-10",
  "songs": [
    {
      "id": 2,
      "title": "Bee Gee"
    },
    {
      "id": 4,
      "title": "Pee Gee"
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
_Song, Genre, Album, or Artist(s) not found._

```json
{
  "message": "Album by id=45 not found."
}
```

**Response (400 Bad Request):**
_Invalid input data (e.g., improper release date format)._

```json
{
  "message": "Validation failed",
  "errors": {
    "releaseDate": "Release date must be in YYYY-MM-DD format"
  }
}
```

---

#### DELETE /api/catalog/albums/{id}

Removes an album from the database by its ID.

**Parameters:**

- `id` (integer, path parameter, required) _Album ID_

**Response (204 No Content):** _Album deleted successfully. No response body is returned._

**Response (404 Not Found):** _Album not found_

```json
{
  "message": "Album by id=5 not found."
}
```

---

### Artist

#### POST /api/catalog/artists

Adds a new artist to the system.

**Request Body Fields:**

- `name` (string, required) _Name of the artist._
- `mainSongIds` (array[integer], optional) _List of song IDs where the artist is the primary performer. Use an empty array `[]` if no songs are assigned yet._
- `featSongIds` (array[integer], optional) _List of song IDs where the artist is a featured (guest) performer. Use an empty array `[]` if no songs are assigned yet._
- `mainAlbumIds` (array[integer], optional) _List of album IDs where the artist is the primary creator. Use an empty array `[]` if no albums are assigned yet._
- `featAlbumIds` (array[integer], optional) _List of album IDs where the artist is a featured/collaborating creator. Use an empty array `[]` if no albums are assigned yet._

**Request Body Example:**

```json
{
  "name": "Mr. Nimbus",
  "mainSongIds": [1, 2],
  "featSongIds": [7],
  "mainAlbumIds": [1],
  "featAlbumIds": []
}
```


**Response (201 Created):**

```json
{
  "id": 10,
  "name": "Mr. Nimbus",
  "songs": [
    {
      "id": 2,
      "title": "Bee Gee"
    },
    {
      "id": 4,
      "title": "Pee Gee"
    }
  ],
  "albums": [
    {
      "id": 1,
      "title": "Kety"
    }
  ]
}
```

**Response (404 Not Found):**
_Invalid input data_

```json
{
  "message": "Song by id=1 was not found"
}
```

---

#### POST /api/catalog/artists/{id}/image

Uploads artist's image (image file) and links the resulting resource URL to the specified artist. If an image is already linked, the existing file is permanently deleted from the server and the URL is overwritten with the new one.

**Parameters:**

- `id` (integer, path parameter, required) _Artist ID_

**Request Headers:**

- `Content-Type: multipart/form-data`

**Request Body (Form-Data):**

- `file` (file/binary, required) _The image file (e.g. JPG, PNG) to be uploaded._

**Response (200 OK):**
_Returns the URL of the uploaded resource._

```json
{
  "message": "Artist image uploaded successfully",
  "imageUrl": "https://s3.aws.com/your-bucket/artists/mr-nimbus.png"
}
```

**Error Response (404 Not Found)**
_Returned when the artist ID does not exist in the database._

```json
{
  "message": "Artist by id=10 was not found."
}
```

**Error Response (400 Bad Request):**
_Returned when the file is missing, empty, or of an unsupported format._

```json
{
  "message": "Invalid file format. Only image/jpeg (JPG) and image/png (PNG) are supported."
}
```

---

#### PATCH /api/catalog/artists/{id}

Partially updates an existing artist's metadata and its relationships. All fields are optional. Only the fields provided in the request body will be modified.

**Parameters:**

- `id` (integer, path parameter, required) _Artists ID_

**Request Body Fields:**

- `name` (string, optional) _Name of the artist._
- `mainSongIds` (array[integer], optional) _List of song IDs where the artist is the primary performer. Use an empty array `[]` to clear songs list._
- `featSongIds` (array[integer], optional) _List of song IDs where the artist is a featured (guest) performer. Use an empty array `[]` to clear songs list._
- `mainAlbumIds` (array[integer], optional) _List of album IDs where the artist is the primary creator. Use an empty array `[]` to clear albums list._
- `featAlbumIds` (array[integer], optional) _List of album IDs where the artist is a featured/collaborating creator. Use an empty array `[]` to clear albums list._

**Request Body Example:**

```json
{
  "name": "Nr. Mimbus"
}
```

**Response (200 OK):** _Artist successfully updated. Returns the updated resource._

```json
{
  "id": 10,
  "name": "Nr. Mimbus",
  "songs": [
    {
      "id": 2,
      "title": "Bee Gee"
    },
    {
      "id": 4,
      "title": "Pee Gee"
    }
  ],
  "albums": [
    {
      "id": 17,
      "title": "Shminty"
    }
  ]
}
```

**Response (404 Not Found):**
_Song, Album, or Artist(s) not found._

```json
{
  "message": "Album by id=45 not found."
}
```

**Response (400 Bad Request):**
_Invalid input data._

```json
{
  "message": "Validation failed",
  "errors": {
    "albumIds": "Only integers allowed in the albumIds array"
  }
}
```

---

#### PUT /api/catalog/artists/{artistId}/albums/{albumId}

Assigns an existing album to the specified artist.

**Parameters:**

- `artistId` (integer, path parameter, required) _Artists ID_
- `albumId` (integer, path parameter, required) _Album ID_

**Response (204 No Content):** _Artist successfully updated._

**Response (404 Not Found):**
_Album, or Artist not found._

```json
{
  "message": "Album by id=45 not found."
}
```

---

#### DELETE /api/catalog/artists/{id}

Removes an artist from the database by its ID.

**Parameters:**

- `id` (integer, path parameter, required) _Artist ID_

**Response (204 No Content):** _Artist deleted successfully. No response body is returned._

**Response (404 Not Found):** _Artist not found_

```json
{
  "message": "Artist by id=5 not found."
}
```

---

### Genre

#### POST /api/catalog/genres

Adds a new genre to the system.

**Request Body Fields:**

- `name` (string, required) _Name of the genre._

**Request Body Example:**

```json
{
  "name": "K-POP"
}
```

**Response (201 Created):**

```json
{
  "id": 10,
  "name": "K-POP"
}
```

**Response (400 Bad Request):**
_Invalid input data_

```json
{
  "message": "Validation failed",
  "errors": {
    "name": "Genre name must be at least 3 characters long"
  }
}
```

---

#### PATCH /api/catalog/genres/{id}

Updates an existing genre's name.

**Parameters:**

- `id` (integer, path parameter, required) _Genre ID_

**Request Body Example:**

```json
{
  "name": "K-Pop"
}
```

**Response (200 OK):** _Genre's name successfully updated._

```json
{
  "id": 10,
  "name": "K-POP"
}
```

**Response (404 Not Found):**
_Genre not found._

```json
{
  "message": "Genre by id=45 not found."
}
```

**Error Response (400 Bad Request):**
_Invalid input data_

```json
{
  "message": "Validation failed",
  "errors": {
    "name": "Genre name must be at least 3 characters long"
  }
}
```
**Error Response (409 Conflict):**
_Genre name is not unique._

```json
{
  "message": "Genre name must be unique."
}
```
---

#### DELETE /api/catalog/genres/{id}

Removes a genre from the database by its ID.

**Parameters:**

- `id` (integer, path parameter, required) _Genre ID_

**Response (204 No Content):** _Genre deleted successfully. No response body is returned._

**Response (404 Not Found):** _Genre not found_

```json
{
  "message": "Genre by id=5 not found."
}
```

---

#### PATCH /api/catalog/genres/{oldId}/transfer-to/{newId}

The service bulk-updates the genre_id field in the associated songs to the new ID

**Parameters:**

- `oldId` (integer, path parameter, required) _Genre ID to be changed in songs_
- `newId` (integer, path parameter, required) _ New genre ID for songs related to `oldId` genre_


**Response (200 OK):** _Genre transferred successfully._
```json
{
  "updatedSongsCount": 15,
  "oldGenreId": 5,
  "newGenreId": 12
}
```
**Response (404 Not Found):** _Either the source (oldId) or target (newId) genre does not exist.


```json
{
  "message": "Genre by id=5 not found."
}
```

**400 Bad Request:** _Provided IDs are identical._

```json
{
  "message": "Source and target genre IDs cannot be the same."
}
```

### Payment Module (`payment`)

#### POST /api/payments/

Creates a new payment record in the local database (status: PENDING), initializes a Stripe Checkout session, and returns the session URL for the user to complete the transaction.

**Request Body:**

```json
{
  "songId": 10,
  "licenseType": "Commercial"
}
```

**Response (201 Created):** _Payment record created and Stripe session initialized successfully._

```json
{
  "paymentId": 45,
  "sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_..."
}
```

**Response (404 Not Found):**
_Returned if the song does not exist in the catalog._

```json
{
  "message": "Song by id=10 not found."
}
```

**Error Response (500 Internal Server Error):**
_Returned if communication with the Stripe API fails._

```json
{
  "message": "Could not initialize payment session with Stripe."
}
```

#### POST /api/webhook/payments

Receives asynchronous event notifications from Stripe (e.g., checkout.session.completed, payment_intent.payment_failed).

**Request Headers:**

- `Stripe-Signature` (string, required) _Used to verify that the event was sent by Stripe._

**Request Body:**

- _Raw JSON payload sent by Stripe containing event detailsResponse._

**Response (204 No content):** _Event successfully received and processed. No body is required by Stripe._

**Error Response (400 Bad Request):**
_Returned when the webhook payload is invalid or the Stripe signature verification fails._

```json
{
  "message": "Webhook signature verification failed."
}
```

**Error Response(500 Internal Server Error):**
_Returned if there is a server-side error while processing the event (e.g., database failure)._

```json
{
  "message": "Error processing webhook event."
}
```

### Licensing Module (`licensing`)

#### GET /api/licenses

Retrieves a list of all licenses owned by the currently authenticated user.

**Response (200 OK):**

```json
[
  {
    "licenseId": 101,
    "songId": 10,
    "licenseType": "Commercial",
    "issuedAt": "2026-07-26T10:15:30Z",
    "status": "ACTIVE"
  },
  {
    "licenseId": 102,
    "songId": 15,
    "licenseType": "Standard",
    "issuedAt": "2026-07-25T14:20:00Z",
    "status": "ACTIVE"
  }
]
```

---

#### GET /api/licenses/{id}

Retrieves the detailsResponse of a specific license owned by the user.

**Response (200 OK):**

```json
{
  "licenseId": 101,
  "songId": 10,
  "songTitle": "Epic Cinematic Theme",
  "licenseType": "Commercial",
  "issuedAt": "2026-07-26T10:15:30Z",
  "certificateUrl": "https://s3.aws.com/your-bucket/certificates/cert_101.pdf",
  "status": "ACTIVE"
}
```

**Error Response (403 Forbidden):**
_Returned if the user attempts to access a license they do not own._

```json
{
  "message": "Access denied. You do not own this license."
}
```

**Error Response (404 Not Found):**
_Returned if the license does not exist._

```json
{
  "message": "License by id=101 not found."
}
```
