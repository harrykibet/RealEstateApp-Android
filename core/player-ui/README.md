lets fix th# Estatia Media UI — Technical Specification

The `player-ui` module provides a high-performance, lifecycle-aware set of Jetpack Compose components designed for immersive, TikTok-style video feeds. It abstracts the complexities of hardware surface management, interaction debouncing, and multi-window adaptability.

## 🖼️ 1. Surface & Hardware Lifecycle

### Native Compose External Surfaces
To achieve TikTok-caliber transition smoothness and correct z-ordering, this module uses the modern `AndroidExternalSurface` API.
-   **Composition Integration**: By using `AndroidExternalSurface` instead of raw `SurfaceView` wrappers, the video surface is correctly integrated into the Compose rendering pipeline, allowing for precise clipping and transformation during page transitions.
-   **Z-Order Management**: Configured with `zOrder = AndroidExternalSurfaceZOrder.Behind` to ensure the video stays below UI overlays (like the heart icon and progress bar) while avoiding the "black flash" timing issues common with legacy interop.
-   **10-bit HDR Support**: Automatically detects and leverages 10-bit color formats for HDR10 and Dolby Vision playback on capable hardware.

## 🖱️ 2. Interaction & Navigation Safety

### Playback Interaction Hardening
The UI maintains strict synchronization with the underlying hardware state:
-   **Single Source of Truth**: The `isPlaying` state is driven exclusively by `Player.Listener.onIsPlayingChanged`. This eliminates the risk of UI/Player desync common in optimistic toggle implementations.
-   **Tap Debounce**: Implemented a **200ms interaction window**. Rapid-fire clicks are filtered out, protecting the hardware decoder from "command spam" and ensuring a stable playback lifecycle.

### Navigation Hygiene
The UI proactively manages resources based on its position in the Navigation Backstack:
-   **Visibility Tracking**: ViewModels use an `isScreenVisible` signal. If a screen is moved to the backstack (e.g., navigating from Home to Search), the UI state is forced to `Idle/Paused`.
-   **Resource Reclamation**: The `EngineVideoPlayer` explicitly pauses playback and yields hardware resources as soon as its Composable is disposed, ensuring backgrounded screens never "steal" the global active player.

## 🚦 3. Advanced UX & State Machine

### Fault-Tolerant UI States
Beyond standard Play/Pause, the UI handles complex real-world conditions:
-   **`Reconnecting`**: Triggered during transient network loss. Surfaces a specialized view and automatically resumes once connectivity returns.
-   **`LowBandwidth`**: A non-intrusive warning overlay shown when sustained congestion (<500kbps) is detected, managing user expectations for quality drops.
-   **`INVALID_URI`**: Catches "Content Errors" (broken database links) at the ViewModel layer. Surfaces an "Unavailable" message immediately, bypassing technical timeout intervals.

## 📱 4. Immersive Integrations

### Picture-in-Picture (PiP)
-   **Automatic Transition**: MainActivity automatically triggers PiP when backgrounded during active playback on supported hardware.
-   **Chrome Suppression**: The root UI layer detects PiP mode and automatically hides all navigation rails, app bars, and snackbars, ensuring the small window is dedicated entirely to the video content.

### System Media Integration
-   **Metadata Surface**: Propagates listing titles and owner names (artists) to the system `MediaSession`.
-   **Hardware Control**: Standard Bluetooth and peripheral "Play/Pause" buttons are mapped directly to the active feed video.

---

## 🏗️ Architecture

-   **`EngineVideoFeed`**: The primary orchestrator for vertical feeds. Handles prefetch coordination and settlement heuristics.
-   **`EngineVideoPlayer`**: Low-level surface management and ExoPlayer binding.
-   **`BaseVideoPlaybackViewModel`**: Standardizes the mapping between engine-level events and the high-fidelity UI state machine.
