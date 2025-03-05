
@Suppress("constPropertyName", "MemberVisibilityCanBePrivate")
object LifecycleDeps {
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"

    val AllLifecycleDeps = listOf(
        lifecycleRuntime,
        viewModelKtx,
        liveDataKtx
    )
}