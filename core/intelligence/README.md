# core:intelligence â€” Recommendation & Personalization Engine

The `intelligence` module serves as the primary hub for Estatia's personalized video experience. It orchestrates the high-fidelity feedback loop between client-side engagement signals and server-side ranking models, ensuring every user sees the properties they are most likely to love.

## ðŸ—ï¸ 1. Recommendation Architecture

Estatia uses a **Hybrid Intelligence** model to achieve TikTok-class feed responsiveness:

### Server-Side Canonical Brain
The "True Source of Truth" for recommendations lives on the server, implemented via an **AppSync Lambda Resolver**. This allows the ranking logic to scale independently and leverage high-performance compute for model inference.
- **Personalized Ranking**: When `fetchPropertiesPaginated(userId)` is called, the resolver fetches candidate properties from Aurora, retrieves user history, and computes match scores.
- **Match Scores**: The server injects a transient `matchScore` (0.0 to 1.0) for every item in the API response.

### Client-Side Execution (Player Engine Awareness)
The `player-engine` remains algorithm-blind but is **Match-Aware**. It consumes the `matchScore` to intelligently allocate hardware and network resources:
- **High Match (>0.9)**: "Deep Warming" â€” Prefetches 3+ segments and prepares a hardware decoder immediately to ensure a zero-latency start.
- **Low Match (<0.4)**: "Speculative Only" â€” Fetches only the manifest to save user data in case the item is skipped.

## ðŸ”„ 2. The Feedback Loop (Telemetry)

The loop is closed by shipping high-fidelity engagement signals from the client back to the server via the **`IEngagementRepository`**. This decouples the intelligence logic from the underlying analytics infrastructure.

### Signal Generation
The `PlaybackAnalyticsListener` and Feature ViewModels capture granular interaction data:
- **Media Watch**: Reports exact `watchTimeMs` and `loopCount`.
- **Interactions**: Reports discrete actions like `LIKE`, `SHARE`, `FOLLOW`, and `COMMENT_OPEN`.
- **Search Context**: Reports search queries and subsequent result selections.

### Architectural Decoupling
By using `IEngagementRepository` as an abstraction:
- **Infrastructure Independence**: The intelligence module doesn't know if signals are shipped via Firebase, a custom Kinesis Firehose, or a local ML model.
- **Unified Pipeline**: Engagement signals are routed through a central implementation in `core:data` that delegates to a high-performance, WorkManager-backed `IAnalyticsTracker`.

## ðŸ“± 3. User Experience Impact

- **Instant Gratification**: By pre-warming high-match content deeper than standard content, the app achieves the "instant-play" feel during scrolls.
- **Data Discipline**: Proactively reduces data waste by not pre-loading large chunks of low-probability content.
- **Continuous Improvement**: Every scroll and loop helps the engine learn the user's preferred property types (e.g., modern apartments vs. rustic cottages).

---

## ðŸ› ï¸ Implementation Details

### Key Classes
- **`EngagementSignalProcessor`**: Manages the local aggregation and shipping of engagement events.
- **`VideoPlaybackCoordinator`**: Interprets match scores to drive prefetch depth.
- **`PlaybackAnalyticsListener`**: The primary sensor for capturing real-time engagement metrics.

### Technical Glossary
| Term | Definition |
|---|---|
| **Match Score** | A 0-1 confidence value injected by the server for a specific User-Listing pair. |
| **Deep Warming** | Concurrent prefetch of segments + hardware player allocation. |
| **Engagement Signal** | A telemetry event containing normalized interaction metrics (e.g., watch %). |


## Dependency Graph
![Module Graph](module_graph.png)

