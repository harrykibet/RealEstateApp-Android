# feature:profile

## Overview
The `profile` module manages the user's personal identity within the app. It provides tools for users to view their activity, edit their personal information, and track their reputation in the ecosystem.

## Features
- **Profile Dashboard**: Summary of user stats (followers, following, property count).
- **Activity Feed**: View history of user interactions and listings.
- **Identity Management**: Edit profile details, including name, bio, and profile picture.

## Technical Details
- **State Management**: Uses `ProfileViewModel` to orchestrate data from `:core:domain`.
- **UI Toolkit**: Material 3 Compose with integrated support for custom backgrounds and themes.

## Dependency Graph
![Module Graph](module_graph.png)
