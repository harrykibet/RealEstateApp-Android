val isCI = System.getenv("CI")?.toBooleanStrict() ?: false

println("Initializing Gradle with CI mode ${if (isCI) "enabled" else "disabled"}")

// Common arguments for both environments
val commonJvmArgs = listOf(
    "-Dfile.encoding=UTF-8",
    "-XX:+UseG1GC",
    "-XX:SoftRefLRUPolicyMSPerMB=1",
    "-XX:+HeapDumpOnOutOfMemoryError",
    "-XX:+AlwaysPreTouch"
)

// Define a data class to store configuration
data class BuildConfig(val jvmArgs: List<String>, val kotlinArgs: List<String>)

// Local development configuration
val localConfig = BuildConfig(
    jvmArgs = commonJvmArgs + listOf(
        "-Xmx2g",
        "-Xms1g",
        "-XX:ReservedCodeCacheSize=256m",
        "-XX:MaxMetaspaceSize=512m",
        "-XX:MaxGCPauseMillis=200",
        "-XX:ParallelGCThreads=2",
        "-XX:ConcGCThreads=1"
    ),
    kotlinArgs = commonJvmArgs + listOf(
        "-Xmx2g",
        "-Xms1g",
        "-XX:ReservedCodeCacheSize=256m",
        "-XX:MaxMetaspaceSize=512m"
    )
)

// CI/CD optimized configuration
val ciConfig = BuildConfig(
    jvmArgs = commonJvmArgs + listOf(
        "-Xmx6g",
        "-Xms2g",
        "-XX:ReservedCodeCacheSize=512m",
        "-XX:MaxMetaspaceSize=1g",
        "-XX:MaxGCPauseMillis=200",
        "-XX:ParallelGCThreads=4",
        "-XX:ConcGCThreads=2"
    ),
    kotlinArgs = commonJvmArgs + listOf(
        "-Xmx6g",
        "-Xms2g",
        "-XX:ReservedCodeCacheSize=512m",
        "-XX:MaxMetaspaceSize=1g"
    )
)

// Select configuration based on environment
val config: BuildConfig = if (isCI) ciConfig else localConfig

// Also set them as system properties explicitly
System.setProperty("org.gradle.jvmargs", config.jvmArgs.joinToString(" "))
System.setProperty("kotlin.daemon.jvmargs", config.kotlinArgs.joinToString(" "))

gradle.settingsEvaluated {
    // Apply JVM args to Gradle daemon
    gradle.startParameter.systemPropertiesArgs = mutableMapOf(
        "org.gradle.jvmargs" to config.jvmArgs.joinToString(" "),
        "kotlin.daemon.jvmargs" to config.kotlinArgs.joinToString(" ")
    )

    // Logging for verification
    println("Configured JVM args for ${if (isCI) "CI" else "local"} environment")
    println("Gradle JVM args: ${config.jvmArgs}")
    println("Kotlin Daemon args: ${config.kotlinArgs}")
}
