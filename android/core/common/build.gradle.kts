plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.core_common"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.test.core.ktx)
    implementation(libs.work.runtime.ktx)

    implementation(libs.ffmpeg.kit.min.gpl)
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(projects.core.model)
}