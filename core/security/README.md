# core:security

## Overview
The `security` module is responsible for protecting sensitive user data and handling authentication state within the app. It provides robust tools for encryption, token management, and secure key storage.

## Key Features
- **Token Management**: Securely stores and retrieves authentication tokens using encrypted storage.
- **Android Keystore Integration**: Uses hardware-backed security where available to protect encryption keys.
- **Data Protection**: Utilities for encrypting and decrypting data at rest.

## Key Components
- `TokenLocalDataSource`: Manages the lifecycle of user sessions.
- `BuildConfigSecureKeyProvider`: Safely exposes sensitive configuration keys.

## Dependency Graph
![Module Graph](module_graph.png)
