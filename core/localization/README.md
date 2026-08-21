# core:localization

## Overview
The `localization` module provides infrastructure for internationalization (i18n) and managing time-related data. It ensures that the app can be easily adapted for multiple languages and time zones.

## Key Features
- **Language Management**: Utilities for dynamically switching app languages and handling localized strings.
- **TimeZone Monitoring**: Real-time tracking of the user's system time zone changes.
- **Repository Pattern**: `LocaleRepository` for persisting and retrieving user language preferences.

## Key Components
- `TimeZoneMonitor`: Interface for observing time zone changes.
- `LocaleRepository`: Manages the application's locale settings.

## Dependency Graph
![Module Graph](module_graph.png)
