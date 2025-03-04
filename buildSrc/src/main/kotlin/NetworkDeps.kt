
@Suppress("ConstPropertyName")
object NetworkDeps {
    // Networking
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttp_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    //const val circuit_breaker = "io.github.resilience4j:resilience4j-circuitbreaker:${Versions.resilience4j}"
    //const val retry = "io.github.resilience4j:resilience4j-retry:${Versions.resilience4j}"
    //const val resilience_kotlin = "io.github.resilience4j:resilience4j-kotlin:${Versions.resilience4j}"

    // Grouped Networking Dependencies
    val AllNetworkDeps = listOf(
        okhttp,
        okhttp_interceptor,
        gson,
        retrofit,
        retrofit_converter_gson
    )
}