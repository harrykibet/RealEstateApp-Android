# feature:chats

## Overview
The `chats` feature module provides the messaging and inbox functionality for the Estatia application. It follows a TikTok-inspired design for the main inbox, featuring a horizontal "Active Now" list and a vertical list of conversations.

## Features
- **Inbox Screen**: A centralized hub for all user communications.
- **Active Now**: Horizontal carousel showing online users with status indicators.
- **Conversation List**: Real-time message previews, timestamps, and unread notification badges.
- **TikTok-style UI**: High-fidelity UI implementation using Jetpack Compose.
- **Mock Data Support**: Built-in ViewModel support for demonstration and testing.

## Module Structure
- `ui/`: Contains the Composable screens and components (e.g., `ChatScreen`, `ActiveNowSection`, `ChatItem`).
- `navigation/`: Handles the integration with the app's centralized navigation system.
- `ChatViewModel.kt`: Manages the UI state, user presence logic, and data loading.
- `ChatUiState.kt`: Defines the sealed interface for the screen's state (Loading, Success, Error).

## Dependencies
This module depends on the following core modules:
- `:core:ui`: Shared UI components and backgrounds.
- `:core:design-system`: Theming, typography, and custom icons.
- `:core:navigation`: Shared route definitions.
- `:core:model`: Data entities like `Chat`, `ChatUser`, and `Message`.
- `:core:common`: Utility classes and extensions.

## Technical Details
- **Architecture**: MVVM with Hilt for dependency injection.
- **State Management**: Uses `StateFlow` and `collectAsStateWithLifecycle` for efficient UI updates.
- **Time Formatting**: Uses `kotlinx-datetime` and `java.time` for localized timestamp formatting.
- **Image Loading**: Integrated with `Coil` for efficient avatar rendering.

## Usage
To navigate to this feature, use the `navigateToChats` extension on `NavController`:
```kotlin
navController.navigateToChats(navOptions)
```

## Screenshots
*(Add screenshots here showing the horizontal Active Now section and vertical chat list)*

## Dependency Graph
![Module Graph](module_graph.png)
