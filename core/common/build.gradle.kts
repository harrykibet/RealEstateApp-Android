plugins {
    alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.common"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.work.runtime.ktx)

    implementation(libs.bundles.lifecycle)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(libs.media3.transformer)
    implementation(libs.media3.effect)
    implementation(libs.media3.common)

    implementation(projects.core.model)

    testImplementation(testFixtures(projects.core.testing))
    androidTestImplementation(testFixtures(projects.core.testing))
}