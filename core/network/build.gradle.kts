plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.firebase)
}

android {
    namespace = "com.estatia.realestate.apps.core.network"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.logging.interceptor)

    implementation(libs.bundles.bouncy)
    implementation(libs.security.crypto.ktx)

    implementation(libs.caffeine)

    implementation(libs.bundles.analytics)

    implementation(libs.bundles.firebase)
    implementation(libs.appauth)
    implementation(libs.amplify.core)
    implementation(libs.amplify.api)
    implementation(libs.amplify.auth)
    implementation(libs.amplify.storage)
    implementation(libs.amplify.analytics)
    implementation(libs.amplify.logging)
    implementation(libs.aws.appconfig)

    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.model)

    testImplementation(testFixtures(projects.core.testingNetwork))
    androidTestImplementation(testFixtures(projects.core.testingNetwork))
}
