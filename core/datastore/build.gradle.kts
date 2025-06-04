plugins {
    alias(libs.plugins.estatia.android.config)
    alias(libs.plugins.estatia.android.testing)
    alias(libs.plugins.estatia.hilt)
}

android {
    namespace = "com.estatia.realestate.apps.core.datastore"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.dataStore)
    implementation(libs.kotlinx.coroutines.test)

    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.datastoreProto)
}