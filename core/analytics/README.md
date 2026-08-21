# core:analytics

## Overview
The `analytics` module provides a unified interface for tracking user interactions, app performance, and system events. It abstracts the underlying analytics providers to allow for easy swapping or multi-provider support.

## Key Features
- **Event Tracking**: Structured tracking of user actions (e.g., likes, searches, property views).
- **Observability**: Real-time monitoring of app state and performance metrics.
- **Background Dispatch**: Uses WorkManager to ensure analytics events are successfully delivered even in poor network conditions.

## Key Components
- `IAnalyticsHelper`: The primary interface for logging events.
- `MetricsTracker`: Handles low-level performance and health monitoring.
- `AnalyticsEvent`: Sealed class defining the global event schema for the app.

## Dependency Graph
![Module Graph](module_graph.png)
