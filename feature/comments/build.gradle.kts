plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.comments"
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
