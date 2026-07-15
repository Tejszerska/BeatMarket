1. Dokumentacja i Konfiguracja
- [ ] Skończyć dokumentację (rozpisanie kontraktów API Request/Response dla płatności).

- [ ] Włączyć zapisywanie logów konsoli do pliku w opcjach startowych IDE, aby nie tracić błędów po restarcie.


2. Architektura i Kod Java

- [ ] Poprawić RegisterController – przenieść ciężką logikę biznesową do odpowiedniego serwisu.

- [ ] Poprawić RegisterController - wywoływanie statusów 4xx .

- [ ] Dodać wartość NONE do enuma SongLanguage (obsługa utworów instrumentalnych).

- [ ] Utworzyć nowego enuma LicenseTier z wartościami STANDARD, COMMERCIAL, BROADCAST.

- [ ] Skonfigurować role w Spring Security (ROLE_CUSTOMER po rejestracji, ROLE_ADMIN do zarządzania zasobami).


3. Baza Danych (Skrypty Flyway)

- [ ] Aktualizacja song: Dodać kolumny preview_url (do próbki) oraz file_url (do pełnego utworu) pod przyszłą integrację z AWS.

- [ ] Nowa tabela song_prices: (Tego zabrakło na Twojej liście) Tabela relacyjna zawierająca kolumny z ceną, walutą, progiem licencyjnym (tier) oraz kluczem obcym do utworu.

- [ ] Nowa tabela payments: Powinna zawierać id użytkownika, status płatności, kwotę oraz obowiązkowo stripe_session_id.

- [ ] Nowa tabela licenses: Powinna zawierać m.in. wygenerowany certyfikat/klucz, identyfikator użytkownika, identyfikator płatności i ramy czasowe ważności.



4. Integracje (Kolejny krok prac)

- [ ] Stworzenie logiki komunikacji ze Stripe (generowanie URL do Checkoutu).

- [ ] Stworzenie ukrytego endpointu Webhooka pod odbieranie potwierdzeń ze Stripe.

- [ ] Podpięcie istniejącego mechanizmu mailowego pod zmianę statusu płatności, by wysyłał powiadomienie po wygenerowaniu licencji.