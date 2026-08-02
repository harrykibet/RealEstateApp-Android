plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.search"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.navigation)

    implementation(projects.core.security)
    implementation(projects.core.playerUi)
    implementation(projects.core.playerEngine)

    implementation(libs.media3.common)
}
