plugins {
    alias(libs.plugins.realestateapp.android.feature)
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
}
