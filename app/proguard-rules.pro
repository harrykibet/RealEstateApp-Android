# ============================================================
# Optional dependency warnings
# ============================================================

# Firestore optional protobuf/google RPC types
-dontwarn com.google.rpc.Status
-dontwarn com.google.type.LatLng
-dontwarn com.google.type.LatLng$Builder

# Micrometer optional integrations
-dontwarn io.micrometer.context.ThreadLocalAccessor
-dontwarn org.LatencyUtils.**

# OpenTelemetry optional auto-configuration SPI
-dontwarn io.opentelemetry.sdk.autoconfigure.spi.**

# SLF4J optional logging integrations
-dontwarn org.slf4j.**

# ============================================================
# Cryptography
# ============================================================

# Conservative initial rule while crypto usage is audited.
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# ============================================================
# WorkManager / Hilt
# ============================================================

-keep class * extends androidx.work.ListenableWorker {
    <init>(
        android.content.Context,
        androidx.work.WorkerParameters
    );
}