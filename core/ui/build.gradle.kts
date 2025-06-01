plugins {
    alias(libs.plugins.estatia.android.config)
    alias(libs.plugins.estatia.android.testing)
    alias(libs.plugins.estatia.android.compose)
    alias(libs.plugins.estatia.hilt)
}

android {
    namespace = "com.estatia.realestate.apps.core.ui"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.lifecycle)

    implementation(libs.metrics.performance)

    implementation(libs.kotlinx.datetime)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(libs.media3.common)
    implementation(libs.media3.ui)

    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.designSystem)
    api(projects.core.analytics)
}