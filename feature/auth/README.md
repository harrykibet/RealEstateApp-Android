# feature:auth

## Overview
The `auth` module provides the complete authentication and onboarding flow for Estatia. It handles user registration, login, password recovery, and multi-factor verification.

## Features
- **Onboarding**: Multi-stage sign-up process with identity validation.
- **Secure Login**: Integration with standard credentials and Google Sign-In.
- **Verification**: Support for email and phone number verification dialogs.
- **Password Recovery**: Automated forgot password workflows.

## Technical Details
- **Architecture**: MVVM with Hilt.
- **Integration**: Communicates directly with the `:core:domain` interfaces for authentication.
- **UI Toolkit**: Built with Jetpack Compose using the centralized `:core:design-system`.

## Dependency Graph
![Module Graph](module_graph.png)
