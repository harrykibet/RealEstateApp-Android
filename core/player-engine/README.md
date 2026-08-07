# core:player-engine

The `player-engine` module is the backbone of media playback in the Estatia app. It provides a robust, thread-safe, and highly optimized orchestration layer on top of **Media3 ExoPlayer**.

## Key Features

- **Adaptive Player Pooling**: Efficiently reuses `ExoPlayer` instances to minimize the overhead of player creation and destruction during fast scrolling in vertical feeds.
- **Thread Confinement**: Enforces strict Main-thread confinement for player mutations to prevent common Media3 threading violations.
- **Smart Prefetching**: Integrates a `MediaCacheWarmer` that prefetches upcoming videos based on scroll direction and priority.
- **CDN Resolution & Health Monitoring**: Dynamically rewrites media URIs to the healthiest CDN endpoint based on real-time latency measurements.
- **Unified Caching**: Uses stable `mediaId` keys to ensure that offline-downloaded content and live playback share the same cache entries.
- **Granular Analytics**: Tracks Quality of Service (QoS) metrics like startup time, buffering duration, and error types per playback session.

## Architecture

The module follows a layered architecture to isolate the feature layer from Media3 internals:

1.  **Orchestration**: `PlayerManager` and `VideoPlaybackCoordinator` manage the high-level playback lifecycle.
2.  **Resource Management**: `PlayerPool` handles the lifecycle of pooled player instances and their associated analytics listeners.
3.  **Configuration**: `PlayerConfigurationFactory` handles URI resolution and player setup (LoadControl, BandwidthMeter, etc.).
4.  **Streaming Stack**: `StreamingPipeline` abstracts the Media3 cache, data sources, and prefetch logic.

## Key Interfaces

- `IPlayerManager`: The primary API for playing, preloading, and observing media state.
- `IStreamingPipeline`: Interface for creating media items and prefetching content.
- `IPlayerConfigurationFactory`: Handles the construction of complex player configurations.

## Threading Model

All interactions with the `PlayerPool` and `ExoPlayer` instances **must** happen on the Main thread. The `PlayerManager` uses a dedicated `playerDispatcher` (aliased to `Dispatchers.Main.immediate`) to ensure all operations are executed safely.
