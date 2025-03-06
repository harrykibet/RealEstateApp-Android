/**
 * `AnalyticsDeps` is an object that centralizes the management of dependencies
 * required for analytics and performance monitoring within the application.
 *
 * It provides a clean and organized way to define, access, and manage various
 * analytics-related libraries and their respective versions.
 *
 * This object defines the dependencies as `VersionedDependency` instances,
 * making it easier to keep track of library versions and update them as needed.
 *
 * It also offers a convenience function, `getAllAnalyticsDeps`, to retrieve all
 * analytics dependencies as a list of dependency strings, which can be directly
 * added to a module's dependencies block.
 */
@Suppress("MemberVisibilityCanBePrivate")
object AnalyticsDeps {
    val guava = Dependency.VersionedDependency(
        group = "com.google.guava",
        name = "guava",
        version = Versions.guava
    ).toGradleNotation

    val androidMetrics = Dependency.VersionedDependency(
        group = "androidx.metrics",
        name = "metrics-performance",
        version = Versions.androidMetrics
    ).toGradleNotation

    val appSet = Dependency.VersionedDependency(
        group = "com.google.android.gms",
        name = "play-services-appset",
        version = Versions.appSet
    ).toGradleNotation

    val openTelemetryApi = Dependency.VersionedDependency(
        group = "io.opentelemetry",
        name = "opentelemetry-api",
        version = Versions.openTelemetry
    ).toGradleNotation

    val openTelemetryExporter = Dependency.VersionedDependency(
        group = "io.opentelemetry",
        name = "opentelemetry-exporter-otlp",
        version = Versions.openTelemetry
    ).toGradleNotation

    val micrometer = Dependency.VersionedDependency(
        group = "io.micrometer",
        name = "micrometer-core",
        version = Versions.micrometer
    ).toGradleNotation

    val micrometerPrometheus = Dependency.VersionedDependency(
        group = "io.micrometer",
        name = "micrometer-registry-prometheus",
        version = Versions.micrometer
    ).toGradleNotation

    val conscrypt = Dependency.VersionedDependency(
        group = "org.conscrypt",
        name = "conscrypt-openjdk-uber",
        version = Versions.conscrypt
    ).toGradleNotation

    // Collect all dependencies in a list for easy retrieval
    fun getAllAnalyticsDeps() = listOf(
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
