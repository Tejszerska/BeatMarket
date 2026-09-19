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

- [ ] obsłużyć kluczowe błedy

```
- Exception.class (Catch-all) na samym dole handlera żeby unikać 500 np Nullpointer,
- MethodArgumentTypeMismatchException jesli błędny  Path Varialable
- MissingServletRequestParameterException brak wymaganego request param
- HttpRequestMethodNotSupportedException gdy dla zdefiniowanego url jest tylko get a ktoś róbuje np post
```

- [ ] walidacja biznesowa w song (RDM) "Czy piosenka może zmienić język, jeśli została już zablokowana przez proces licencyjny?", "Czy można nałożyć zniżkę na utwór, który ma status archiwalny?", "Czy data premiery nie jest przypadkiem z przyszłości?".

- rozważyć i zakodować CascadeType w encjach catalog

- [ ] spr SongPriceId.class - czy dobrze zaimplementowałam?

- [ ] Dodawanie plików do AWS (audio i image ) i obsługa w bazie danych (moduł catalog) ---- rozważyć w jaki sposób zabezpieczyć te pola w samej encji Song,
  aby uniemożliwić ich przypadkową zmianę z zewnątrz (np. poprzez usunięcie standardowych setterów i wprowadzenie dedykowanych metod assignFile(...)?)

- [ ] Dodawanie wyceny licencji piosenki - komunikacja między modułami catalog, licensing

- [ ] Spring HATEOAS do response edycji piosenki (ceny licencji - tabela pricing, edycja zasobów url)

- [ ] Stworzenie logiki komunikacji ze Stripe (generowanie URL do Checkoutu).

- [ ] Stworzenie ukrytego endpointu Webhooka pod odbieranie potwierdzeń ze Stripe.

- [ ] Podpięcie istniejącego mechanizmu mailowego pod zmianę statusu płatności, by wysyłał powiadomienie po wygenerowaniu licencji.