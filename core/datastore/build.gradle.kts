plugins {
    alias(libs.plugins.realestateapp.android.config)
    alias(libs.plugins.realestateapp.android.testing)
    alias(libs.plugins.realestateapp.hilt)
}

android {
    namespace = "com.application.real_estate_app.core_datastore"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.dataStore)
    implementation(libs.kotlinx.coroutines.test)

    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.datastoreProto)
}