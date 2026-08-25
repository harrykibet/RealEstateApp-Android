# Core Testing Network

This module provides testing utilities specifically for network operations, including mocks and helpers for API testing.

## Why was this split from core:testing?

Splitting the network testing utilities into a dedicated module provides several benefits:

1.  **Reduced Dependency Leakage**: Basic unit tests in other modules don't need to transitively pull in networking dependencies (like Retrofit or MockWebServer) unless they are specifically testing network layers.
2.  **Improved Build Performance**: Changes to network testing logic only trigger recompilation for modules that depend on it, rather than the entire project depending on a monolithic `core:testing`.
3.  **Better Separation of Concerns**: Keeps the base `core:testing` module lean and focused on core infrastructure (like base test rules and DI helpers).
