plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.property"
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.material)

    implementation(libs.navigation.fragment.ktx)

    implementation(libs.gson)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(projects.core.model)
    implementation(projects.core.testing)

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    implementation(libs.bundles.lifecycle)
}
