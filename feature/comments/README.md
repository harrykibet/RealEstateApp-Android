# feature:comments

## Overview
The `comments` module manages user engagement through discussions on property listings. It is designed to be highly reusable and is typically integrated as a bottom sheet within the home and search feeds.

## Features
- **Real-time Discussions**: View and post comments on properties.
- **Optimized UI**: Uses a performant bottom sheet implementation that doesn't disrupt the main feed experience.
- **Engagement States**: Handles loading, empty, and error states for comment threads.

## Integration
This module is primarily used as a child component by other feature modules. It receives a `propertyId` and manages its own internal state via `CommentsViewModel`.

## Dependency Graph
![Module Graph](module_graph.png)
