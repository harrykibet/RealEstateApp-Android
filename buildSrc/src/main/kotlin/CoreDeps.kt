
@file:Suppress("ConstPropertyName")
object CoreDeps {
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val viewPager2 = "androidx.viewpager2:viewpager2:${Versions.viewPager}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayout}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentKtx}"
    const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityKtx}"
    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashScreen}"
    const val testCoreKtx = "androidx.test:core-ktx:${Versions.testCoreKtx}"
    const val workRuntimeKtx = "androidx.work:work-runtime-ktx:${Versions.workManager}"

    val CommonCoreDependencies = listOf(
        coreKtx,
        appCompat,
        fragmentKtx,
        activityKtx
    )
    val CoreUiDependencies = listOf(
        material,
        constraintLayout,
        recyclerView,
        viewPager2,
        swipeRefreshLayout
    )
}
