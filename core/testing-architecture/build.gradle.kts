plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

dependencies {
    implementation(projects.core.architecture)
    implementation(libs.konsist)
    testImplementation(libs.junit.junit)
}
