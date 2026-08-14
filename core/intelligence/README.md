# core:intelligence — Estatia Trust & Personalization Engine

The `intelligence` module is the architectural "brain" of Estatia. It orchestrates on-device AI, personalized ranking, and platform integrity guardrails to deliver a high-trust, TikTok-class video experience.

## 🏗️ 1. Recommendation Architecture

Estatia uses a **Hybrid Intelligence** model to achieve extreme responsiveness:

### Server-Side Canonical Brain
The "Source of Truth" for recommendations lives in an **AWS AppSync Lambda Resolver**.
- **Personalized Ranking**: When the client requests a feed, the resolver retrieves user engagement history from Aurora, runs a ranking inference, and returns a pre-sorted list.
- **Match Scores**: The server injects a transient `matchScore` (0.0 to 1.0) into every property model.
- **Dynamic ABR Ladder**: The server automatically transcodes all uploaded videos into a multi-rendition HLS ladder (360p, 720p, 1080p). The client uses the `hlsUrl` master manifest to perform segment-by-segment adaptive switching.

### Client-Side Execution (Match-Aware)
The `player-engine` consumes these scores and manifests to drive hardware resource allocation:
- **Deep Warming (>0.9)**: Concurrent prefetch of 3+ segments + hardware decoder allocation for zero-latency starts. Prefetching biases toward the **1080p rendition** for high-match content.
- **Speculative Only (<0.4)**: Fetches only the manifest to minimize data waste on low-probability content. Initial playback starts at **360p** to save user data.

## 📦 2. Standardized Content Pipeline

To ensure a "Golden Source" for server-side transcoding, Estatia implements a client-side hygiene layer:

- **Local Compression**: Uses **Media3 Transformer** to normalize all user-recorded videos to a high-quality 1080p/30fps MP4 before upload. This ensures consistent input for the backend Rendition Factory.
- **Hybrid Fallback**: The client natively supports both HLS (preferred) and flat MP4 playback, ensuring compatibility for legacy content.

## 🛡️ 3. Content Safety & Integrity

The module implements a "Layered Defense" safety system via **`IContentSafetyService`**.

### On-Device Proactive Moderation (UX Layer)
Uses ML Kit to block unsafe content *before* it leaves the device:
- **Text Toxicity**: Scans descriptions and comments for abusive language using heuristic patterns.
- **Visual Guardrails**: Scans photos for violence or explicit material.
- **Video Keyframe Analysis**: Extracts strategic keyframes from videos (e.g., 5 frames spread across duration) and runs visual moderation on each.
- **Integrity Checks**: Detects phone numbers/emails in descriptions to prevent platform bypass.

### Redundancy Strategy
> [!IMPORTANT]
> On-device moderation is for **Immediate UX Feedback**. For production security, the Estatia backend performs a second, high-precision analysis (AWS Rekognition) on all media once uploaded to the S3 ingestion bucket.

## 🤝 3. Estatia Trust Framework

Used to verify the authenticity of agents and property listings via **`IVerificationService`**.

- **Identity Verification**: On-device OCR extracts Government ID data, which is then compared against a real-time selfie using face-mesh matching.
- **Active Liveness**: Infrastructure for checking human liveness (blinks/head turns) in video verification.
- **Proof of Physical Presence**: Verifies signed media metadata (GPS/Timestamp) to prove an agent was physically at the property during recording.

## 📊 4. The Feedback Loop (Telemetry)

Captured via **`IEngagementRepository`**, this loop feeds the server-side ML models:
- **Media Watch**: Exact `watchTimeMs` and `loopCount`.
- **Discrete Actions**: Standardized `LIKE`, `SHARE`, `SAVE`, and `COMMENT_OPEN` signals.
- **Search Context**: Queries and result clicks are reported to refine the user's "Taste Profile."

## 🛠️ 5. Implementation Stack
- **ML Engine**: Google ML Kit (On-Device).
- **Video Analysis**: `MediaMetadataRetriever` for keyframe extraction.
- **Dependency Injection**: Hilt `@Singleton` provision via `IntelligenceModule`.

## Dependency Graph
![Module Graph](module_graph.png)
