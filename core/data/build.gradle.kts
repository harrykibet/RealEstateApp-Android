plugins {
    alias(libs.plugins.estatia.android.core)
    alias(libs.plugins.estatia.firebase)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.estatia.realestate.apps.core.data"
}

dependencies {

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.bundles.firebase)

    implementation(libs.security.crypto.ktx)

    implementation(libs.kotlinx.datetime)

    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    ksp(project(":core:ksp-architecture"))
    implementation(projects.core.model)
    api(projects.core.domain)
    implementation(projects.core.datastore)
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.security)
    implementation(projects.core.network)

    testImplementation(testFixtures(projects.core.testingNetwork))
    androidTestImplementation(testFixtures(projects.core.testingNetwork))
}
