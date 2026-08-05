plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.property"
}

dependencies {

    implementation(projects.core.designSystem)
    implementation(projects.core.ui)
    implementation(projects.core.localization)
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.common)

    implementation(libs.navigation.fragment.ktx)

    implementation(libs.gson)

    implementation(projects.core.testing)

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    implementation(libs.bundles.lifecycle)
}
