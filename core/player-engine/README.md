# Estatia Media Engine — Technical Specification

The `player-engine` module is a high-performance, resilient, and resource-aware orchestration layer built on top of **Media3 ExoPlayer**. It is engineered to deliver a fluid, "TikTok-class" video experience while maintaining strict device stability and battery discipline.

## 🏗️ 1. Performance Architecture

### Fling Heuristic & Adaptive Debounce
To minimize CPU and network churn during rapid navigation ("surfing"), the engine implements a velocity-aware debounce mechanism.
-   **Standard Dwell**: 100ms settle window before committed playback.
-   **Fling Mode**: If >3 pages are flipped in 300ms, the settle window increases to 250ms and neighbor preloading is suspended until velocity drops.
-   **Jank-Awareness**: If the UI thread is dropping frames (>20% jank ratio), the engine automatically increases debounce to 400ms, prioritizing scroll smoothness over autoplay latency.

### Symmetric & Deep Prefetching
The engine maintains a deep look-ahead window with **Priority Decay** to ensure seamless forward and backward scrolling.
-   **Visible**: 100% byte budget (High priority).
-   **Next (N+1)**: 70% budget.
-   **Previous (N-1)**: 40% budget (Ensures instant back-scroll).
-   **Far-Ahead (N+2)**: 20% budget (Speculative bytes only).

## 🛡️ 2. Fault Tolerance & Reliability

### BOLA-lite (Buffer-Aware ABR)
The ABR policy incorporates real-time buffer occupancy to proactively prevent stalls.
-   **Critical Threshold**: If buffer < 2s, the bitrate cap is slashed by 50% regardless of throughput.
-   **Precautionary Threshold**: If buffer < 5s, a 20% reduction is applied to allow the buffer to stabilize.

### Robust Network Recovery
The engine distinguishes between transient signal loss and terminal errors.
-   **Reconnecting State**: Detected network failures surface a "Reconnecting" UI rather than a hard error.
-   **Auto-Retry**: The system automatically prepares and resumes interrupted streams as soon as `INetworkStateProvider` reports a return to connectivity.

### Buffering Watchdog
A 15-second safety timer monitors every "Buffering" state. If the hardware decoder or network stream hangs without reporting an error, the watchdog triggers a synthetic recovery flow to unblock the UI.

## 🔋 3. Resource & Power Discipline

### Granular Thermal Tiers
Playback load is dynamically shed based on the device's physical temperature (`PowerManager.thermalStatus`).
-   **Moderate**: 30% bitrate reduction.
-   **Severe**: 65% bitrate reduction + **HDR Suppression** (disables high-GPU-load rendering).
-   **Critical**: 85% bitrate reduction + 360p resolution cap.

### Adaptive Storage Budgeting
The media cache ceiling is not static. It is calculated on every startup as `min(512MB, availableStorage * 0.1)`, preventing the app from triggering system-level "Storage Full" warnings on constrained devices.

### Graceful Backgrounding
The engine implements a two-stage lifecycle response:
-   **Immediate**: Playback pauses and audio focus is abandoned.
-   **60s Grace Timer**: Hardware decoders are preserved for quick app-switches. After 60 seconds of inactivity, all pooled player instances are fully released.

## 📱 4. Hardware & System Integration

### Adaptive Player Pooling
Reuses `ExoPlayer` instances to eliminate the 300-700ms overhead of player creation. 
-   **Hardware Bounding**: The pool size is hard-capped by the device's physical hardware decoder limits (`MediaCodecInfo.maxSupportedInstances`).
-   **Memory-Safe Management**: Uses `WeakHashMap` for listener tracking, ensuring that released player instances are eligible for garbage collection immediately.
-   **Lightweight Refilling**: Implements an optimized "Idle creation" path that constructs hardware objects without the overhead of CDN resolution or configuration parsing.

### Automatic Decoder Fallback
If a hardware decoder fails to initialize for high-efficiency formats (AV1/HEVC), the engine automatically retries the request with a `codec=h264_baseline` override, ensuring content visibility on all hardware.

### Picture-in-Picture & Media Sessions
-   **Seamless PiP**: Automatically enters PiP mode on Home-press if a video is active.
-   **System Controls**: Integrates with `MediaSession` to provide lock-screen metadata and standard Bluetooth/peripheral hardware controls.

---

## 🛠️ 5. Diagnostics & Telemetry
-   **ChaosDataSource**: Includes a fault-injection fuzzer to simulate stalls, random IO failures, and bandwidth throttling deterministically in Debug builds.
-   **Integrated Metrics**: Wired to Micrometer with automatic logging of startup latency and buffering duration, enabling data-driven verification of engine performance in the field.

## 🧵 Threading Model
All mutations to pooled player state are strictly confined to the **Main thread**. Thread safety is enforced via `checkConfinement()` assertions in all core scheduling classes (`PlayerPool`, `VideoPlaybackCoordinator`, etc.), preventing race conditions by construction.
