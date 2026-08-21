# feature:payments

## Overview
The `payments` module provides a centralized, secure, and self-contained payment processing system for the Estatia application. It follows a vertical ownership model, meaning it owns the checkout UI, business logic, and integration with the domain layer.

## Architecture
The module is designed around **Option 1: Full Vertical Ownership**. 
- **Caller Decoupling**: Other features (e.g., `feature:property`, `feature:market`) trigger a payment by navigating to a typed route. They do not need to know about payment providers, idempotency, or transaction logic.
- **Typed Result Channel**: Results are passed back to the calling feature via `SavedStateHandle` using a shared `PaymentResult` contract.

## Key Components

### 1. Checkout Flow
- **PaymentsScreen**: A generic, Material 3-based checkout screen that adapts based on the `PaymentContext` (Booking, Boost, Subscription).
- **Processing States**: Built-in support for `Idle`, `Processing`, `Success`, and `Error` states with retry functionality.

### 2. Domain & Data Integration
- **ProcessPaymentUseCase**: Orchestrates the payment transaction, handling amount conversion and repository interaction.
- **IPaymentsRepository**: Defined in `:core:domain` and implemented in `:core:data`.
- **Remote Data Sources**: Supports both **AWS (AppSync/GraphQL)** and **Firebase (Cloud Functions)** backends with real status propagation (no more hardcoded success).

### 3. Navigation Contract
The module exposes a clear contract in `:core:navigation`:
```kotlin
@Serializable
data class PaymentRoute(
    val referenceId: String,
    val amount: Double,
    val currency: String,
    val context: PaymentContext
)
```

## How to Use

### 1. Triggering a Payment
From any feature, use the `navigateToPayment` extension:
```kotlin
navController.navigateToPayment(
    referenceId = "booking_123",
    amount = 1500.0,
    currency = "KSh",
    context = PaymentContext.BOOKING
)
```

### 2. Observing the Result
The calling feature should observe the `PAYMENT_RESULT_KEY` in its `SavedStateHandle` or directly in the navigation graph:
```kotlin
// In the NavHost or calling Screen
val result = navController.currentBackStackEntry
    ?.savedStateHandle
    ?.getStateFlow<PaymentResult?>(PAYMENT_RESULT_KEY, null)
    ?.collectAsStateWithLifecycle()
```

## Technical Details
- **Architecture**: MVVM with Hilt.
- **State Management**: `StateFlow` and `collectAsStateWithLifecycle`.
- **Security**: Designed to isolate PCI-sensitive data handling (future-ready).
- **Error Handling**: Uses `AppResult` for robust, type-safe error propagation from the data source to the UI.

## Dependency Graph
![Module Graph](module_graph.png)
