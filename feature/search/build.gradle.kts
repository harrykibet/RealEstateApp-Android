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

    implementation(libs.bundles.play)
    implementation(libs.maps.compose)


    implementation(libs.bundles.navigation)

    implementation(projects.core.security)
    implementation(projects.core.model)
}
