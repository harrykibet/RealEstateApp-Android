# feature:settings

## Overview
The `settings` module provides the configuration interface for the application. It allows users to customize their experience, manage their accounts, and review legal/compliance information.

## Features
- **Account Management**: Safe logout and session management.
- **Theme Customization**: Toggle between dark/light mode and system defaults.
- **Compliance & Legal**: Links to legal documentation, privacy policies, and terms of service.

## Integration
The module communicates with `:core:datastore` for theme settings and `:core:domain` for account-related operations.

## Dependency Graph
![Module Graph](module_graph.png)
