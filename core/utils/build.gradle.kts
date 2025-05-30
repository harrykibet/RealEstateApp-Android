plugins {
    alias(libs.plugins.estatia.android.config)
    alias(libs.plugins.estatia.android.testing)
    alias(libs.plugins.estatia.firebase)
    alias(libs.plugins.estatia.hilt)
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