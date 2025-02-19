
@file:Suppress("ConstPropertyName")
object CoreDeps {
    const val coreKtx = "androidx.core:core-ktx:${Versions.androidx_core_ktx}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.androidx_app_compat}"
    const val material = "com.google.android.material:material:${Versions.google_android_material}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.androidx_constraint_layout}"
    const val viewPager2 = "androidx.viewpager2:viewpager2:${Versions.androidx_view_pager2}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.androidx_recycler_view}"
    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.androidx_swipe_refresh_layout}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.androidx_fragment_ktx}"
    const val activityKtx = "androidx.activity:activity-ktx:${Versions.androidx_activity_ktx}"
    const val splashScreen = "androidx.core:core-splashscreen:${Versions.androidx_core_splash_screen}"
    const val testCoreKtx = "androidx.test:core-ktx:${Versions.androidx_test_core_ktx}"
    const val workRuntimeKtx = "androidx.work:work-runtime-ktx:2.9.0"

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
