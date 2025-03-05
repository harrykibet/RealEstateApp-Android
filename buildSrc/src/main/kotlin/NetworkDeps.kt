/**
 * `NetworkDeps` is an object that encapsulates the dependencies related to networking in the project.
 * It provides easy access to commonly used libraries like OkHttp, Gson, and Retrofit, along with their specific versions.
 *
 * This object also includes a utility function, `getAllNetworkDeps()`, which conveniently retrieves all
 * the declared networking dependencies as a list of strings suitable for build files.
 */
@Suppress("MemberVisibilityCanBePrivate")
object NetworkDeps {
    val okhttp = Dependency.VersionedDependency(
        group = "com.squareup.okhttp3",
        name = "okhttp",
        version = Versions.okhttp
    )

    val okhttpInterceptor = Dependency.VersionedDependency(
        group = "com.squareup.okhttp3",
        name = "logging-interceptor",
        version = Versions.okhttp
    )

    val gson = Dependency.VersionedDependency(
        group = "com.google.code.gson",
        name = "gson",
        version = Versions.gson
    )

    val retrofit = Dependency.VersionedDependency(
        group = "com.squareup.retrofit2",
        name = "retrofit",
        version = Versions.retrofit
    )

    val retrofitConverterGson = Dependency.VersionedDependency(
        group = "com.squareup.retrofit2",
        name = "converter-gson",
        version = Versions.retrofit
    )

    // Function to Retrieve All Networking Dependencies
    fun getAllNetworkDeps() = listOf(
        okhttp,
        okhttpInterceptor,
        gson,
        retrofit,
        retrofitConverterGson
    ).map { it.get() }
}
