plugins {
    alias(libs.plugins.estatia.android.feature)
}

android {
    namespace = "com.estatia.realestate.apps.feature.property"
}

dependencies {

    implementation(projects.core.playerUi)
    implementation(projects.core.playerEngine)
    implementation(libs.media3.common)
    implementation(projects.core.localization)
    implementation(projects.core.intelligence)

    implementation(libs.navigation.fragment.ktx)

    debugImplementation(testFixtures(projects.core.testing))

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.camerax)
    implementation(libs.androidx.activity.compose)
    implementation(libs.accompanist.permissions)
}
