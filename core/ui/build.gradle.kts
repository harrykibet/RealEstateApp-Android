plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.android.compose)
}

android {
    namespace = "com.estatia.realestate.apps.core.ui"
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

    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.playerUi)
    implementation(projects.core.designSystem)
    api(projects.core.analytics)
}