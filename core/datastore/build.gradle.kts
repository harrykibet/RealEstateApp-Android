plugins {
    alias(libs.plugins.estatia.android.core)
}

android {
    namespace = "com.estatia.realestate.apps.core.datastore"
}

dependencies {

    implementation(libs.androidx.dataStore)
    implementation(libs.kotlinx.coroutines.test)

    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.datastoreProto)

    testImplementation(testFixtures(projects.core.testing))
    androidTestImplementation(testFixtures(projects.core.testing))
}