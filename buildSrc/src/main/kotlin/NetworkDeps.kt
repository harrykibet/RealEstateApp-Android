
@Suppress("ConstPropertyName")
object NetworkDeps {
    // Networking
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttp_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val cronet = "org.chromium.net:cronet-embedded:119.0.6045.41"

    // Grouped Networking Dependencies
    val allNetworkDependencies = listOf(
        okhttp,
        okhttp_interceptor,
        cronet
    )
}