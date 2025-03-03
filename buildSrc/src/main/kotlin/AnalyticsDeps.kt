
@Suppress("constPropertyName")
object AnalyticsDeps {
    const val guava = "com.google.guava:guava:${Versions.guava}"
    const val androidMetrics = "androidx.metrics:metrics-performance:${Versions.androidMetrics}"
    const val appSet = "com.google.android.gms:play-services-appset:${Versions.appSet}"
    const val openTelemetryApi = "io.opentelemetry:opentelemetry-api:${Versions.openTelemetry}"
    const val openTelemetryExporter = "io.opentelemetry:opentelemetry-exporter-otlp:${Versions.openTelemetry}"
    const val micrometer = "io.micrometer:micrometer-core:${Versions.micrometer}"
    const val micrometerPrometheus = "io.micrometer:micrometer-registry-prometheus:${Versions.micrometer}"
    const val conscrypt = "org.conscrypt:conscrypt-openjdk-uber:${Versions.conscrypt}"

    val AllAnalyticsDependencies = listOf(
        guava,
        androidMetrics,
        appSet,
        openTelemetryApi,
        openTelemetryExporter,
        micrometer,
        micrometerPrometheus,
        conscrypt
    )
}