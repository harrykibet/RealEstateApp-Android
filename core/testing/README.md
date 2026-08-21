# core:testing

## Overview
The `testing` module provides a shared suite of tools, fake data, and utility classes to facilitate high-quality unit and instrumentation testing across the Estatia project.

## Key Features
- **Mock Data Sets**: Pre-configured mock users, properties, and chat messages for consistent test results.
- **Rule Extensions**: Custom JUnit rules and Compose test helpers.
- **Dependency Fakes**: Mock implementations of repositories and data sources for isolated unit tests.

## Key Components
- `MockProperties`: A collection of standardized property listings for UI testing.
- `MockUsers`: Diverse user profiles for testing authentication and profile flows.

## Usage
Include this module as a `testImplementation` or `androidTestImplementation` dependency in your feature or core module.

## Dependency Graph
![Module Graph](module_graph.png)
