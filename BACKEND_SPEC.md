# Estatia Backend & Intelligence Specification

Estatia is a "Smart-Client" application. While it uses managed services (Firebase/AWS) for basic persistence, its high-performance video experience and trust framework rely on several custom server-side components.

This document outlines the additional backend logic that must be implemented for the application to function at a production level.

---

## 🧠 1. Personalized Recommendation Engine
**Infrastructure**: AWS AppSync + Direct Lambda Resolver + Amazon Aurora.

The client calls `getPersonalizedFeed(userId)`. The backend must:
- **Retrieve Context**: Fetch the user's recent engagement history (likes, watch durations, skips) from the telemetry table.
- **Run Inference**: Use a ranking model (e.g., Collaborative Filtering or a Deep Learning model on SageMaker) to sort candidate property listings.
- **Inject Match Scores**: Every item in the response **must** include a `matchScore` (0.0 to 1.0). This score is critical for the client-side player engine to perform match-aware prefetching.

## 📦 2. Standardized Video Pipeline (The "Rendition Factory")
**Infrastructure**: AWS S3 + Lambda + AWS Elemental MediaConvert.

The client uploads a high-quality "Golden Source" MP4. The backend must:
- **Trigger on Upload**: Detect new files in the S3 ingestion bucket.
- **Transcode to HLS**: Generate a multi-rendition HLS ladder:
    - `360p` (Low bandwidth fallback)
    - `720p` (Standard)
    - `1080p` (High Definition)
- **Generate Master Manifest**: Create a `master.m3u8` file that groups these renditions.
- **Update Metadata**: Update the property record in Aurora/Firestore with the new `hlsUrl`.

## 🛡️ 3. High-Precision Content Moderation
**Infrastructure**: AWS Rekognition / Google Cloud Vision + Lambda.

While the client performs zero-latency safety checks, the backend is the **Source of Truth**.
- **Asynchronous Sweep**: Once media is uploaded, run a high-precision ML scan on the full video/image.
- **Integrity Enforcement**: If the server-side model detects prohibited content (Explicit material, violence, PII) that the on-device model missed, it must flag the listing for admin review and hide it from the public feed.

## 🤝 4. Trust & Identity Verification
**Infrastructure**: Lambda + External Identity APIs.

- **ID Cross-Referencing**: Verify the extracted Government ID data against national databases.
- **Status Promotion**: Transition user `VerificationLevel` from `IDENTITY_VERIFIED` to `TRUSTED_PARTNER` only after successful asset (deed) verification.

---

## 🛠️ Developer Note on Telemetry
Engagement signals (reported via `IEngagementRepository`) are shipped as high-fidelity business events. The backend must ingest these into a data warehouse (Redshift/BigQuery) to continuously retrain the recommendation models.
