plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.feature_property"
}

dependencies {
    implementation(libs.bundles.android)
    implementation(libs.material)

    implementation(libs.navigation.fragment.ktx)

    implementation(libs.gson)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.common)

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    implementation(libs.bundles.lifecycle)
}
