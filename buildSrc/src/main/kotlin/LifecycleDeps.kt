
@file:Suppress("ConstPropertyName")
object LifecycleDeps {
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidx_lifecycle}"
    const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidx_lifecycle}"
    const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.androidx_lifecycle}"

    val AllLifecycleDependencies = listOf(
        lifecycleRuntime,
        viewModelKtx,
        liveDataKtx
    )
}