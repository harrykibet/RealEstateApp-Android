
@Suppress("ConstPropertyName")
object NetworkDeps {
    // Networking
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttp_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

    // Grouped Networking Dependencies
    val allNetworkDependencies = listOf(
        okhttp,
        okhttp_interceptor
    )
}