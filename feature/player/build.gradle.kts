plugins {
    alias(libs.plugins.realestateapp.android.feature)
}

android {
    namespace = "com.application.real_estate_app.feature_player"
}

dependencies {
    implementation(libs.bundles.media3)

    implementation(projects.core.domain)
    implementation(projects.core.network)
}
