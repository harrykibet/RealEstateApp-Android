
@Suppress("ConstPropertyName")
object NetworkDeps {
    // Networking
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttp_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"

    // Grouped Networking Dependencies
    val allNetworkDependencies = listOf(
        okhttp,
        okhttp_interceptor,
        gson
    )
}