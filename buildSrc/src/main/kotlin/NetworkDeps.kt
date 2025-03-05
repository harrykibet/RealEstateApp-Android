
@Suppress("constPropertyName", "MemberVisibilityCanBePrivate")
object NetworkDeps {
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttp_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"

    // Grouped Networking Dependencies
    val AllNetworkDeps = listOf(
        okhttp,
        okhttp_interceptor,
        gson,
        retrofit,
        retrofit_converter_gson
    )
}