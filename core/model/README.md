# core:model

## Overview
The `model` module is a pure Kotlin library that contains the application's domain models and data entities. By keeping this module free of Android dependencies, it can be easily shared between the app, testing, and potentially other platforms.

## Key Categories
- **User Models**: `UserDomainModel`, `UserData`, `UserPreferences`.
- **Property Models**: `ListingUiModel`, `MediaType`, `Money`.
- **Feature Models**: `Chat`, `Message`, `MarketItem`, `PaymentResult`.
- **System Models**: `NetworkState`, `TimeZone`.

## Guidelines
- This module should **not** contain any logic other than simple mapping functions or data validation.
- All models should be serializable if they are intended to be passed through navigation or stored in a persistent layer.

## Dependency Graph
![Module Graph](module_graph.png)
