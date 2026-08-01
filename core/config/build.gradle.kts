plugins {
    alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.config"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.domain)
}
