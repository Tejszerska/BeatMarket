# General Project Instructions

Write code that is consistent with Bob Martin's Clean Code principles. Ensure all generated code strictly adheres to the
project's existing:

- Code style
- Code structure
- Naming conventions
- Formatting conventions
- Commenting conventions

# Architecture

- The project is a Modular Monolith. Strictly respect the boundaries of business modules (e.g., account, catalog,
  licensing, payment).
- The project uses a Pragmatic Hexagonal Architecture mixed with a Layered Architecture.
- Avoid overengineering: JPA annotations (like @Entity) and Spring Data interfaces are explicitly allowed and expected
  inside the domain packages. Do not generate separate pure domain models and database entities if not necessary.
- Use Ports and Adapters to communicate between the domain and infrastructure.
- Keep external integrations and low-level technical details (e.g., audio parsing, S3 storage, security) strictly inside
  the infrastructure package.

# Test

Tests always have the `@DisplayName` annotation.

- Display name describes what the test does.
- Display name is always in the present tense.
- Display name is always in the active voice.
- Display name is always in the singular form.

The test method name is a concise version of the `@DisplayName` value.

- The test method name is written in `snake_case`.
- The test method name does not contain unnecessary words.
- The test method name is always descriptive enough to understand what the test does without reading the code.

Structure test bodies using the **Given-When-Then** (or Arrange-Act-Assert) pattern, visually separating each section
with a blank line.