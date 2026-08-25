plugins {
   alias(libs.plugins.estatia.android.core)
   alias(libs.plugins.estatia.firebase)
}

android {
    namespace = "com.estatia.realestate.apps.core.analytics"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.model)

    implementation(libs.work.runtime.ktx)
    implementation(libs.bundles.analytics)
    implementation(libs.bundles.firebase)
    implementation(projects.core.common)

}
