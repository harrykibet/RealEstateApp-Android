plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.android.compose)
}

android {
    namespace = "com.estatia.realestate.apps.core.player_ui"
}

dependencies {
    implementation(libs.bundles.media3)
    implementation(libs.google.accompanist)

    implementation(projects.core.playerEngine)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.designSystem)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.mockkAndroid)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}