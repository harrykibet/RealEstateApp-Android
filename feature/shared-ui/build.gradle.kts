plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.shared_ui"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.bundles.lifecycle)
    implementation(libs.metrics.performance)
    implementation(libs.kotlinx.datetime)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(libs.media3.common)
    implementation(libs.media3.ui)
    implementation(libs.media3.exoplayer)

    implementation(projects.core.playerUi)
    implementation(projects.core.localization)

    testImplementation(testFixtures(projects.core.testing))
    androidTestImplementation(testFixtures(projects.core.testing))
}
