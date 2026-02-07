plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.firebase)
}

android {
    namespace = "com.estatia.realestate.apps.core.data"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.bundles.firebase)
    implementation(libs.play.services.maps)

    implementation(libs.security.crypto.ktx)

    implementation(libs.kotlinx.datetime)

    implementation(projects.core.model)
    implementation(projects.core.datastore)
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.security)
    implementation(projects.core.network)
}