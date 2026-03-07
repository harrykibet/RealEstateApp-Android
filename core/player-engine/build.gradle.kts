plugins {
    alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.player_engine"
}

dependencies {
    implementation(libs.bundles.media3)

    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.config)
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.network)
}