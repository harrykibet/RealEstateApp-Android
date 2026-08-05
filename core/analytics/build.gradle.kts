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

    implementation(libs.bundles.firebase)
}
