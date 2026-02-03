# Feature:player module review — TikTok-style media player

## Executive summary

The **feature:player** module has a **strong skeleton** (Media3, caching, ABR, DRM, analytics, HDR, overlays) but **core playback is not implemented**. The main entry point used by the app—`IExoplayer` implemented by `ExoPlayerInstanceManager`—has **attach, detach, resume, and pause as TODO**, and **no media is ever loaded** into the player. As a result, video does not play anywhere in the app. To reach a **world-class, TikTok-style** level you need to complete the playback pipeline, unify the “one active player per feed” model, and fix integration bugs.

---

## What “TikTok-style” usually means

| Capability | Your module | World-class expectation |
|------------|------------|---------------------------|
| Vertical feed with one active video | Scroll logic in `PropertyAdapter` (good) | ✅ Concept present |
| Autoplay when item is fully visible | Intended but attach is no-op | ✅ Fix attach + play |
| Pause when scrolled away | `pauseCurrentVideo` / `releaseExoPlayer` | ✅ Fix detach/pause |
| Mute / unmute toggle | Not in `IExoplayer` or UI | ❌ Add |
| Loop current clip | Not implemented | ❌ Add `repeatMode = REPEAT_MODE_ONE` |
| Preload next item | `ContentPreloader` + `preloadMedia` (cache-only) | ⚠️ Optional: ExoPlayer preload |
| Tap to play/pause | Commented out in adapter | ❌ Re-enable and wire |
| Fullscreen | Not present | Optional |
| PiP | Not present | Optional |
| Lock screen / notification controls | `MediaSessionManager` (single player) | ⚠️ Wire to active player |
| Low latency (live) | `LowLatencyStreamer` (unused) | Optional |
| HDR | `VideoRendererView.enableHDR()` (unused in current UI) | Optional |
| Overlays (stickers, links) | `InteractiveOverlayEngine` / `OverlayManager` (use `VideoRendererView`) | Optional; current UI uses `PlayerView` |

---

## Critical gaps (why video doesn’t play)

### 1. **IExoplayer core methods are unimplemented**

- `attachPlayerToView(PlayerView, mediaId)` — **TODO**
- `detachPlayer()` — **TODO**
- `resume()` / `pause()` — **TODO**

So the app never attaches a player to the view, never loads media, and never pauses/resumes.

### 2. **No media is ever set on the player**

- `acquirePlayer(mediaId)` returns (or creates) an `ExoPlayer` but **never calls `setMediaItem()` / `setMediaItems()`**.
- So even if you attached that player to a `PlayerView`, nothing would play.

### 3. **mediaId vs URL confusion**

- Interface uses `mediaId`; `PropertyAdapter` sometimes passes **URL** to `attachPlayerToView` (e.g. `property.videoUrls.first()` or `videoUrl`), and `PropertyCard` passes **property.id**.
- You need a **single, clear contract**: e.g. “attach(view, mediaId, mediaUrl)” so the implementation can both **key the player** (for release) and **load the media** (from URL).

### 4. **Player pool and “current” player**

- Pool reuses “any non-playing player” without clearing or re-preparing for new media → wrong video could play.
- `resume()` / `pause()` have no effect because there is no notion of **current** player; the implementation must track the player that is attached to the visible view and call `play()` / `pause()` on it.

---

## Architecture and integration issues

### 5. **Two sources of ExoPlayer**

- `PlayerModule` provides a **single** `ExoPlayer` `@Singleton`.
- `ExoPlayerInstanceManager` maintains its **own pool** and never uses that singleton.
- `MediaSessionManager` is constructed with the **singleton** player, so notification/lock screen controls are not tied to the **active feed player**. For TikTok-style, media session should reflect the currently playing item.

**Recommendation:** Use one strategy: either a **single shared player** that is reassigned to the visible item (simpler, TikTok-like) or a **pool** managed by `ExoPlayerInstanceManager` with a clear “active” player for resume/pause and media session.

### 6. **VideoRendererView vs PlayerView**

- **Overlay / HDR:** `VideoRendererView` (with overlay container and HDR) is used by `InteractiveOverlayEngine` and `OverlayManager`.
- **App UI:** `PropertyCard` and `PropertyAdapter` use plain **`PlayerView`** (Compose `AndroidView` or XML).
- So overlays and HDR are **never** used in the current feed. To use them, the feed would need to use `VideoRendererView` (or a Compose equivalent) and the same attach/detach flow.

### 7. **VideoRendererView and DI**

- `VideoRendererView` uses `@Inject lateinit var deviceUtils: DeviceUtils` (and similar). Views created via XML or `PlayerView(context)` are **not** in the Hilt graph, so these fields are never injected and will throw at runtime if used.

**Recommendation:** Remove `@Inject` from the view; pass dependencies via constructor or a factory, or resolve them from a context that has access to the app component.

### 8. **FavoritesScreen — wrong injection**

- `val exoPlayer: IExoplayer = hiltViewModel()` — `hiltViewModel()` returns a **ViewModel**, not `IExoplayer`. This is a type/compile bug and will crash or fail at runtime.

**Recommendation:** Inject `IExoplayer` via `@Composable` parameters or a CompositionLocal (provided at app level where the player is available).

### 9. **PlayerControls play/pause callback**

- `setPlayPauseListener(listener: (Boolean) -> Unit)` calls `listener(it.isSelected)` **before** toggling `it.isSelected`, so the callback receives the **old** state. Callers that rely on “current playing state” will get the wrong value.

**Recommendation:** Call the listener **after** toggling, or pass the **new** state, e.g. `listener(!it.isSelected)`.

---

## Streaming, cache, and preload

### 10. **CacheManager**

- **Prefetch:** Uses `DataSourceInputStream`; in Media3 the preferred way to fill the cache is often `CacheWriter` or the equivalent API. Confirm that `DataSourceInputStream` exists in your Media3 version and that reading from `CacheDataSource` actually writes into the cache; otherwise switch to the recommended caching API.
- **clearEntireCache:** Calls `cache.release()` then `SimpleCache.delete(...)`. After `release()`, the cache instance is invalid; ensure no other code holds references. Prefer clearing in a controlled way (e.g. remove resources) if the cache is shared.

### 11. **ContentPreloader**

- **Metered network:** Preload only when `!networkUtils.isNetworkMetered()` (WiFi) — good.
- **Worker:** Each `schedulePreload` can start a new coroutine; under load you may have many concurrent workers. Prefer a **single worker** and a queue (e.g. `Channel` or a shared queue with one `launch`).

### 12. **ChunkDownloader**

- Same pattern: multiple `startDownloadWorker()` calls can spawn many coroutines. Use one worker and a single queue.

---

## What’s in good shape

- **Media3 / ExoPlayer** and module structure (core, ui, streaming, drm, analytics, services, advanced).
- **Caching:** `CacheManager` with `SimpleCache`, eviction, and cache status.
- **ABR:** `ABRStrategy` considers network, battery, and device (bitrate caps).
- **DRM:** `WidevineManager` and license callback setup.
- **Analytics:** `PlaybackQualityService` (startup time, buffering) and `QoSEventLogger`; need to be wired to the **actual** player instance.
- **HDR:** `HdrConfiguration` and `VideoRendererView.enableHDR()` (usable once this view is in the playback path).
- **Feed scroll logic:** `PropertyAdapter` “one visible item” and preload-next logic are the right ideas; they just need a working attach/detach and one active player.

---

## Recommended implementation order

1. **Implement playback in `ExoPlayerInstanceManager`:**
   - Treat second parameter of `attachPlayerToView` as **mediaUrl** (or add `mediaUrl` and keep `mediaId` for release).
   - In `attachPlayerToView`: get or create player, `setMediaItem(MediaItem.fromUri(mediaUrl))`, `playerView.player = player`, `prepare()` / `playWhenReady = true`, and track **current** view/player/mediaId.
   - Implement `detachPlayer()` (clear view’s player, pause), `resume()` / `pause()` on current player, and `releasePlayer(mediaId)` using the same key used at attach.

2. **Unify call sites:**
   - All callers that need playback must pass a **URL** into attach (e.g. `property.videoUrls.first()` in `PropertyCard`; adapter already has URL). Use a single key (id or url) for acquire/release so adapter and card don’t mix id vs url inconsistently.

3. **Fix FavoritesScreen:**  
   - Get `IExoplayer` from composition (e.g. parameter or `LocalIExoplayer.current`) instead of `hiltViewModel()`.

4. **Fix PlayerControls:**  
   - Invoke the play/pause listener with the **new** state (e.g. after toggling, or pass `!it.isSelected`).

5. **Optional but valuable for “world-class” feel:**
   - Mute/unmute in UI and in player.
   - Loop: `player.repeatMode = Player.REPEAT_MODE_ONE` for the current item.
   - Re-enable tap-to-play/pause in the adapter and wire it to the same player.
   - Connect `MediaSessionManager` to the **active** ExoPlayer (the one used for the visible feed item).
   - Consider a **single** shared ExoPlayer for the feed (reassign source on scroll) to simplify lifecycle and media session.

---

## Summary table

| Area | Status | Action |
|------|--------|--------|
| Playback (attach/detach/load) | Broken (TODO) | Implement in ExoPlayerInstanceManager |
| Resume / Pause | Broken (TODO) | Track current player; call play/pause |
| Feed scroll ↔ play/pause | Designed | Works once attach/detach/resume/pause work |
| Mute / Loop / Tap | Missing or commented | Add for TikTok-like UX |
| Preload | Cache-only | Optional: ExoPlayer preload next |
| Media session | Single unused player | Wire to active player |
| FavoritesScreen | Wrong type (ViewModel) | Inject IExoplayer correctly |
| PlayerControls | Wrong callback state | Pass new state after toggle |
| HDR / Overlays | Implemented but not in feed | Use VideoRendererView in feed if needed |

Once the core attach/detach/resume/pause and media loading are implemented and call sites pass URLs consistently, the module will be in a position to match world-class, TikTok-style behavior for a vertical feed.
