plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.firebase)
    alias(libs.plugins.realestateapp.hilt)
    alias(libs.plugins.realestateapp.android.packaging)
    alias(libs.plugins.realestateapp.android.testing)
}

android {
    namespace = "com.application.real_estate_app.core"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.test.core)
    implementation(libs.core.testing)
    implementation(libs.work.runtime.ktx)

    implementation(libs.metrics.performance)
    implementation(libs.play.services.appset)
    implementation(libs.guava)

    implementation(libs.bundles.firebase)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.ffmpeg.kit.min.gpl)

    // Glide Dependencies
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(libs.bundles.networking)
}
