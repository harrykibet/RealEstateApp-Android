plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.core_utils"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.firebase)
    implementation(libs.firebase.config)

    implementation(libs.ffmpeg.kit.min.gpl)

    implementation(projects.core.interfaces)
    implementation(projects.core.common)
    implementation(projects.core.model)

    implementation(libs.work.runtime.ktx)

    implementation(libs.glide)
    ksp(libs.glide.compiler)
}