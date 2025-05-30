plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.home"
}

dependencies {

    implementation(libs.bundles.android)

    implementation(projects.core.model)
    implementation(projects.core.domain)

    implementation(libs.bundles.navigation)

    implementation(libs.bundles.lifecycle)
}
