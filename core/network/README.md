# core:network

The `network` module manages all external data communication, providing a robust layer over Firebase and future REST/GraphQL APIs. It is responsible for request execution, authentication, and mapping remote data models to local entities.

## Key Responsibilities

- **Remote Data Access**: Implements data sources for properties, users, search, and more.
- **Authentication**: Wraps Firebase Auth for email/password, Google sign-in, and phone verification.
- **Resilience**: Provides a unified `INetworkClient` with customizable retry policies (e.g., `ExponentialRetryPolicy`).
- **Error Mapping**: Translates infrastructure-specific exceptions (Firebase, OkHttp) into consistent domain-level [AppResult] types.
- **Network Monitoring**: Tracks device connectivity state and provides real-time updates via [INetworkStateProvider].

## Key Interfaces

- `INetworkClient`: The core engine for executing requests with retry logic.
- `IPropertyRemoteDatasource`: Manages property listings, uploads, and interactions.
- `IAuthRemoteDataSource`: Handles the user lifecycle and authentication flows.
- `INetworkStateProvider`: Provides a reactive stream of the device's connectivity status.

## Data Models

The module uses `EntityModel` classes (e.g., `PropertyEntityModel`) which represent the structure of data as it exists on the server. These are mapped to domain models in the `core:data` module.
