# feature:market

## Overview
The `market` module is a core ecosystem feature of Estatia. It is designed to be much more than a simple directory; it is a **property ecosystem** that supports users through every stage of the property lifecycle—from discovery and moving to maintenance, improvement, and management.

The strategic goal of the Market feature is:
> **Helping users not only find a property, but also everything they need to move into, maintain, improve, and eventually leave that property.**

## Core Pillars
The Market is structured around three primary marketplace pillars:

### 1. Services (Problem → Provider)
A marketplace for home-related services where users can find professionals to solve specific problems.
- **Home Maintenance**: Plumbers, electricians, carpenters, painters, etc.
- **Cleaning & Household**: General cleaning, deep cleaning, fumigation, waste collection.
- **Moving & Relocation**: House movers, packing services, furniture assembly.
- **Internet & Utilities**: Fiber installation, solar maintenance, CCTV setup.

### 2. Products (Property-Specific Commerce)
A curated marketplace for items needed for the property, avoiding general electronics or unrelated goods.
- **Furnishing**: Sofas, beds, tables, curtains, carpets.
- **Appliances**: Kitchen equipment, bathroom fixtures.
- **Home Décor**: Lighting, mirrors, and security products.

### 3. Property Professionals
Connecting users with the professional network required for property transactions and management.
- **Legal & Finance**: Property lawyers, mortgage providers, insurance agents.
- **Evaluation**: Property inspectors, valuers, surveyors.
- **Management**: Real estate agents, property managers, developers.

## Key Features
- **Categorized Discovery**: High-fidelity UI with horizontal carousels for popular services, products, and professionals.
- **Trust Layer**: Integrated provider verification (Identity, Business, Professional, Trusted levels) and transaction-based reviews.
- **Project Marketplace ("Post a Need")**: Users can describe a complex need (e.g., "I want to renovate my kitchen") and receive tailored proposals from professionals.
- **Lifecycle Integration**: Deeply connected with the search and rental flows. For example, after renting an apartment, the app automatically suggests moving and cleaning services.

## Technical Details
- **Architecture**: MVVM with Hilt for dependency injection.
- **UI Toolkit**: Built entirely with **Jetpack Compose (Material 3)**.
- **State Management**: Uses `StateFlow` and `collectAsStateWithLifecycle` for performant, lifecycle-aware UI updates.
- **Data Modeling**: Comprehensive domain models in `:core:model` for `MarketItem`, `MarketProvider`, and `MarketProject`.

## Module Structure
- `ui/`: Composable screens (`MarketScreen`) and reusable components (`MarketCard`, `MarketSection`).
- `navigation/`: Extension functions for `NavController` and `NavGraphBuilder` to handle seamless integration into `EstatiaNavHost`.
- `MarketViewModel.kt`: Orchestrates data fetching, search, and category filtering.
- `MarketUiState.kt`: Sealed interface representing the screen states (Loading, Success, Error).

## Usage
To navigate to the Market tab:
```kotlin
navController.navigateToMarket(navOptions)
```

## Dependency Graph
![Module Graph](module_graph.png)
