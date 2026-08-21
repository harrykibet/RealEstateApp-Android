# app

## Overview
The `app` module is the entry point for the Estatia Android application. It orchestrates the various feature modules, sets up the global navigation host, and provides the top-level application and activity classes.

## Responsibilities
- **Navigation**: Defines the `EstatiaNavHost` which connects all feature graphs.
- **Dependency Injection**: Sets up the Hilt Singleton Component and provides app-wide dependencies.
- **Global UI**: Implements the `EstatiaApp` Composable, including the bottom navigation bar and snackbar host.
- **Process Orchestration**: Manages app-level lifecycles, background synchronization, and deep link resolution.

## App State
The module uses a centralized `EstatiaAppState` class to manage navigation logic, network status monitoring, and unread notification states in a reactive way.

## Dependency Graph
![Module Graph](module_graph.png)
