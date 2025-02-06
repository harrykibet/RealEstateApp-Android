
@Suppress("ConstPropertyName")
object NetworkDeps {
    // Networking
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.squareup_okhttp3}"
    const val okhttp_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.squareup_okhttp3}"
    const val gson = "com.google.code.gson:gson:${Versions.google_code_gson}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.squareup_retrofit2}"
    const val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.squareup_retrofit2}"

    // Grouped Networking Dependencies
    val allNetworkDependencies = listOf(
        okhttp,
        okhttp_interceptor,
        gson,
        retrofit,
        retrofit_converter_gson
    )
}