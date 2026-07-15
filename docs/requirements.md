## Requirements:

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
- [ ] The customer can see all their previously bought licences 
---

### Phase III: Web Interface (WIP)

#### Frontend Implementation
- [ ] The final phase introduces a dedicated React single-page application for the frontend.
- [ ] This graphical user interface is built to seamlessly consume the underlying REST API, completely replacing the initial Swagger UI testing flow and providing a full experience for both customers and creators.
