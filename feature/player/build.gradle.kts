plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.player"
}

dependencies {
    implementation(libs.bundles.media3)

    implementation(projects.core.domain)
    implementation(projects.core.network)
}
