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

    implementation(projects.core.designSystem)
}
