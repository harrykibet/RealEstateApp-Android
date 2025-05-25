plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.android.compose)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.core_ui"
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
    api(projects.core.analytics)
}