# core:player-ui

The `player-ui` module provides a set of Jetpack Compose components and state management utilities for building high-performance video experiences.

## Key Components

- **`EngineVideoFeed`**: A robust vertical pager designed for TikTok-style video feeds. It handles page visibility callbacks to coordinate prefetching and playback with the underlying engine.
- **`EngineVideoPlayer`**: A low-level component that manages the lifecycle of a video surface (using `SurfacePool`) and handles the attachment/detachment of `ExoPlayer` instances.
- **`PlaybackErrorView`**: A reusable UI component for displaying playback errors with specific iconography and a retry mechanism.
- **`BaseVideoPlaybackViewModel`**: A base class for ViewModels that need to coordinate with the `VideoPlaybackCoordinator`. It ensures that playback state is correctly observed and isolated to the specific screen.

## State Management

The module uses `PlayerUiState` to represent the current state of a video player. This state is derived from the engine's `PlaybackStateReducer.State` and includes:
- `Idle`, `Buffering`, `Ready`, `Playing`, `Paused`, `Ended`
- `Error(message, type)`: Supports categorized errors (Network, Server, etc.) for better UX.

## Surface Optimization

To minimize the overhead of surface creation, this module uses a `SurfacePool`. When a video player is scrolled off-screen, its surface is released back to the pool and can be immediately reused by the next video player entering the viewport. This prevents the "black flash" and layout shifts often associated with dynamic surface allocation.
