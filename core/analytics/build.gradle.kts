plugins {
   alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.analytics"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
}
