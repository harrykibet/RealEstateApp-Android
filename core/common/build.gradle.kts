plugins {
    alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.common"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.work.runtime.ktx)

    implementation(libs.glide)
    ksp(libs.glide.compiler)

    implementation(projects.core.model)
}