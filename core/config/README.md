# core:config

## Overview
The `config` module manages the application's dynamic configuration. It allows for enabling/disabling features, tweaking parameters, and handling environment-specific settings without requiring a new app release.

## Key Features
- **Remote Config Integration**: Connects with backend providers (e.g., AWS AppConfig) to fetch live settings.
- **Role-Based Assets**: Configuration is split into specialized JSON files in assets to improve maintenance and reduce merge conflicts:
    - `network_config.json`: Base URLs and endpoints.
    - `security_config.json`: Security patterns and observability flags.
    - `player_tuning_config.json`: Performance parameters for Media3.
    - `chaos_config.json`: Parameters for fault-injection testing.
- **ISP-Compliant API**: Exposes specific interfaces (`INetworkConfig`, `ISecurityConfig`, etc.) so clients only see the configuration they need.

## Key Components
- `IConfigProvider`: Monolithic interface for initialization and full access.
- `INetworkConfig`, `ISecurityConfig`, `IPlayerTuningConfig`, `IChaosConfig`: Specialized interfaces for segregated access.
- `ConfigProvider`: Orchestrates loading from split assets and remote fetching.

## Dependency Graph
![Module Graph](module_graph.png)
