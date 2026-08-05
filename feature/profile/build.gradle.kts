plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.profile"
}

dependencies {
    implementation(projects.core.designSystem)
    implementation(projects.core.ui)
    implementation(projects.core.localization)
    implementation(projects.core.model)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.navigation)
    implementation(libs.bundles.lifecycle)

    implementation(libs.eventbus)
}
