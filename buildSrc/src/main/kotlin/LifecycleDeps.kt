/**
 * Object containing dependencies related to the Android Lifecycle library.
 *
 * This object provides convenient access to common Lifecycle components like `lifecycle-runtime-ktx`,
 * `lifecycle-viewmodel-ktx`, and `lifecycle-livedata-ktx`. It also offers a function to retrieve
 * all these dependencies as a list of dependency strings suitable for Gradle configuration.
 */
@Suppress("MemberVisibilityCanBePrivate")
object LifecycleDeps {
    val lifecycleRuntime = Dependency.VersionedDependency(
        group = "androidx.lifecycle",
        name = "lifecycle-runtime-ktx",
        version = Versions.lifecycle
    )

    val viewModelKtx = Dependency.VersionedDependency(
        group = "androidx.lifecycle",
        name = "lifecycle-viewmodel-ktx",
        version = Versions.lifecycle
    )

    val liveDataKtx = Dependency.VersionedDependency(
        group = "androidx.lifecycle",
        name = "lifecycle-livedata-ktx",
        version = Versions.lifecycle
    )

    // Function to retrieve all lifecycle dependencies
    fun getAllLifecycleDeps() = listOf(
        lifecycleRuntime,
        viewModelKtx,
        liveDataKtx
    ).map { it.get() }
}
