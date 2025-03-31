plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.hilt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    namespace = "com.application.real_estate_app.feature_comments"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(libs.bundles.navigation)

    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.common)
}
