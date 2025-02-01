
@Suppress("ConstPropertyName")
object NetworkDeps {
    // Networking
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.squareup_okhttp3}"
    const val okhttp_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.squareup_okhttp3}"
    const val gson = "com.google.code.gson:gson:${Versions.google_code_gson}"

    // Grouped Networking Dependencies
    val allNetworkDependencies = listOf(
        okhttp,
        okhttp_interceptor,
        gson
    )
}