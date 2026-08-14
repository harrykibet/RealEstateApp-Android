# Estatia Disaster Recovery & Backup Strategy

This document outlines the strategy for ensuring data durability, service availability, and business continuity for the Estatia platform.

---

## 🏗️ 1. Recovery Objectives

- **Recovery Point Objective (RPO)**: 
    - **Critical Data (Aurora/Firestore)**: < 5 minutes.
    - **Media Assets (S3)**: < 1 hour.
- **Recovery Time Objective (RTO)**: 
    - **Regional Failover**: < 15 minutes (Automated).
    - **Full System Reconstruction**: < 4 hours.

---

## 🗄️ 2. Database Backup Strategy

### Amazon Aurora (Primary System Data)
- **Point-in-Time Recovery (PITR)**: Enabled with a 35-day retention period. Allows restoration to any second within the window.
- **Aurora Global Database**: Cross-region replication to a secondary region (e.g., `us-east-1` to `eu-west-1`). Failover is handled via AWS Route 53 Application Recovery Controller.
- **Snapshots**:
    - Automated daily snapshots.
    - Manual monthly snapshots (Retained for 1 year for legal compliance).

### Cloud Firestore (Engagement & Metadata)
- **PITR**: Enabled via Google Cloud console (7-day window).
- **Scheduled Exports**: Daily export of all collections to a regional GCS/S3 bucket using Cloud Functions + Cloud Scheduler.
- **Regional Redundancy**: Multi-region deployment enabled to protect against single-datacenter outages.

---

## 📦 3. Media & Storage Durability

### Amazon S3 (Rendition & Ingestion)
- **Versioning**: Enabled on all buckets to protect against accidental deletes or ransomware.
- **Cross-Region Replication (CRR)**: The "Distribution" bucket (containing HLS ladders) is replicated to a secondary region to ensure global availability even during a primary region outage.
- **Lifecycle Policies**: Move legacy "Source" MP4s to S3 Glacier after 90 days to reduce costs while maintaining a deep backup.

---

## 📡 4. Regional Failover Protocol

### API Layer (AppSync / Gateway)
- **Route 53 Health Checks**: Configured with failover routing. If the primary regional endpoint returns 5xx errors, traffic is automatically routed to the secondary regional endpoint.
- **Client-Side Fallback**: The Android application is configured with a prioritized list of `apiEndpoints`. If the primary `baseUrl` times out consistently, the `NetworkClient` will attempt a one-time failover to the secondary region.

### CDN Layer
- **Multi-CDN Strategy**: Implemented via `CdnFailoverDataSource`. 
- **Segment-Level Failover**: If a regional CDN node fails, the client-side player engine instantly switches to a different provider (CloudFront -> Akamai -> local backup).

---

## 🛡️ 5. Operational Readiness

- **Annual Drills**: The engineering team performs an "Active Failover" drill once per year, where the primary region is manually shut down to verify the RTO/RPO targets.
- **Infrastructure as Code (IaC)**: All infrastructure is defined via Terraform/CDK, ensuring the secondary region is a pixel-perfect mirror of the primary.
