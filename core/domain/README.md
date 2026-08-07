# core:domain

The `domain` module contains the purest form of business logic in the application. It is completely independent of Android frameworks, UI, or specific data storage implementations.

## Key Components

- **Domain Models**: Immutable data classes representing business entities (e.g., `PropertyDomainModel`, `AuthUserDomainModel`).
- **Use Cases**: Encapsulate single, reusable units of business logic (e.g., `GetPropertyUseCase`, `TogglePropertyLikeUseCase`).
- **Repository Interfaces**: Define the contracts for data access, implemented in the `core:data` module.

## Principles

1.  **Framework Independence**: Should not import any `android.*` or `androidx.*` packages (except for lightweight annotations).
2.  **Stability**: Changes in API versions or database schemas should not leak into this module.
3.  **Testability**: Logic is easily unit-testable without mocks for Android components.
