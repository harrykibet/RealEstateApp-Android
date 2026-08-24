plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.android.room)
}

android {
    namespace = "com.estatia.realestate.apps.core.database"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.model)
    implementation(projects.core.common)

    testImplementation(testFixtures(projects.core.testing))
    androidTestImplementation(testFixtures(projects.core.testing))
}