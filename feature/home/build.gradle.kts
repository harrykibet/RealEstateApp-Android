plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.home"
}

dependencies {

    implementation(projects.core.model)
    implementation(projects.core.navigation)
    implementation(projects.core.playerUi)
    implementation(projects.core.playerEngine)

    implementation(libs.bundles.navigation)
    implementation(libs.media3.common)

    implementation(libs.bundles.lifecycle)
}
