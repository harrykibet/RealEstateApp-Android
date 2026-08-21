# core:notifications

## Overview
The `notifications` module handles the presentation and management of system-level notifications. It provides an abstraction over the Android NotificationManager to simplify the process of showing alerts to the user.

## Responsibilities
- **System Tray Integration**: Showing push notifications and local alerts.
- **Channel Management**: Organizing notifications into appropriate categories (e.g., messages, property updates).
- **Deep Linking**: Handling user interaction with notifications to open specific screens in the app.

## Key Components
- `Notifier`: The primary interface for triggering notifications.
- `SystemTrayNotifier`: The standard implementation for Android system notifications.

## Dependency Graph
![Module Graph](module_graph.png)
