1. Dokumentacja i Konfiguracja
- [x] Skończyć dokumentację (rozpisanie kontraktów API Request/Response dla płatności).

- [x] Włączyć zapisywanie logów konsoli do pliku w opcjach startowych IDE, aby nie tracić błędów po restarcie.


2. Architektura i Kod Java

- [x] Poprawić RegisterController – przenieść ciężką logikę biznesową do odpowiedniego serwisu.

- [x] Poprawić RegisterController - wywoływanie statusów 4xx .

---
- Dopasować endpointy `catalog` do API contracts
  **Artists (Artyści)**
* [x] GET /api/catalog/artists
* [ ] GET /api/catalog/artists/{id}
* [ ] POST /api/catalog/artists
* [ ] PUT /api/catalog/artists/{id}
* [ ] DELETE /api/catalog/artists/{id}

**Albums (Albumy)**
* [ ] GET /api/catalog/albums
* [ ] GET /api/catalog/albums/{id}
* [ ] POST /api/catalog/albums
* [ ] PUT /api/catalog/albums/{id}
* [ ] DELETE /api/catalog/albums/{id}

**Songs (Utwory)**
* [x] GET /api/catalog/songs
* [x] GET /api/catalog/songs/{id}
* [x] POST /api/catalog/songs
* [x] PATCH /api/catalog/songs/{id}
* [x] DELETE /api/catalog/songs/{id}

**Genres (Gatunki)**
* [x] GET /api/catalog/genres
* [x] GET /api/catalog/genres/{id}
* [x] POST /api/catalog/genres
* [ ] PATCH  /api/catalog/genres/{id}
* [ ] DELETE /api/catalog/genres/{id}

---


- [x] Biblioteka JsonNullable (Standard w OpenAPI) - będę jej używać do odcinania relacji przy PATCH np. /api/catalog/songs/{songId}
  Umożliwi rozdzielenie pól o wartości null (usuwanie relacji) oraz pól nie przysłanych (niezmienianie wartości niewpisanych pól)

- [ ] Poprawić Security Filter Chain - rozważyc grupowanie metod

```
// SONGS endpoint rules
  .requestMatchers(HttpMethod.GET, "/api/catalog/songs/**").permitAll()
  .requestMatchers(
  HttpMethod.POST,
  HttpMethod.PUT,
  HttpMethod.PATCH,
  HttpMethod.DELETE
  ).hasRole("ADMIN") // Zastosuje się do URL-i podanych w kontekście, lub można doprecyzować ścieżkę
```

- [x] Poprawić sens biznesowy relacji dotychczasowych tabel:
```
- Song <-> Artist @ManyToMany (wł. Song) umożliwi "featy"
- Song -> Album @ManyToOne (wł. Song) błąd mapowania po stronie Album użyć mappedBy = "album", usuwając @JoinColumn
- Album <-> Artysta @ManyToMany zostaje, bo album ma głównego wykonawce
```

- [x] Dodać wartość NONE do enuma SongLanguage (obsługa utworów instrumentalnych) (jednak INSTRUMENTAL już było wpisane)

- [x] Utworzyć nowego enuma LicenseTier z wartościami STANDARD, COMMERCIAL, BROADCAST.

- [x] Skonfigurować role w Spring Security (ROLE_CUSTOMER po rejestracji, ROLE_ADMIN do zarządzania zasobami).

- [ ] ujednolicić błędy - MissingRequiredFieldException dla wszystkich pól required ( Rich Domain Model refactor w encjach na wzór Song)

- [ ] obsłużyć kluczowe błedy

```
- Exception.class (Catch-all) na samym dole handlera żeby unikać 500 np Nullpointer,
- MethodArgumentTypeMismatchException jesli błędny  Path Varialable
- MissingServletRequestParameterException brak wymaganego request param
- HttpRequestMethodNotSupportedException gdy dla zdefiniowanego url jest tylko get a ktoś róbuje np post
```

- [ ] walidacja biznesowa w song (RDM) "Czy piosenka może zmienić język, jeśli została już zablokowana przez proces licencyjny?", "Czy można nałożyć zniżkę na utwór, który ma status archiwalny?", "Czy data premiery nie jest przypadkiem z przyszłości?".

- rozwać i zakodować CascadeType w encjach catalog

3. Baza Danych (Skrypty Flyway)

- [x] Aktualizacja song: Dodać kolumny preview_url (do próbki) oraz file_url (do pełnego utworu) pod przyszłą integrację z AWS, .

- [x] Aktualizacja album: Dodać kolumnę cover_url

- [x] Aktualizacja artist: Dodać kolumnę image_url

- [x] Nowa tabela song_prices: Tabela relacyjna zawierająca kolumny z ceną, walutą, progiem licencyjnym (tier) oraz kluczem obcym do utworu.

- [x] Nowa tabela payments: Powinna zawierać id użytkownika, status płatności, kwotę oraz obowiązkowo stripe_session_id.

- [x] Nowa tabela licenses: Powinna zawierać m.in. wygenerowany certyfikat/klucz, identyfikator użytkownika, identyfikator płatności i ramy czasowe ważności.


4. Integracje (Kolejny krok prac)


- [ ] Dodawanie plików do AWS (audio i image ) i obsługa w bazie danych (moduł catalog) ---- rozważyć w jaki sposób zabezpieczyć te pola w samej encji Song,
  aby uniemożliwić ich przypadkową zmianę z zewnątrz (np. poprzez usunięcie standardowych setterów i wprowadzenie dedykowanych metod assignFile(...)?)

- [ ] Dodawanie wyceny licencji piosenki - komunikacja między modułami catalog, licensing

- [ ] Spring HATEOAS do response edycji piosenki (ceny licencji - tabela pricing, edycja zasobów url)

- [ ] Stworzenie logiki komunikacji ze Stripe (generowanie URL do Checkoutu).

- [ ] Stworzenie ukrytego endpointu Webhooka pod odbieranie potwierdzeń ze Stripe.

- [ ] Podpięcie istniejącego mechanizmu mailowego pod zmianę statusu płatności, by wysyłał powiadomienie po wygenerowaniu licencji.