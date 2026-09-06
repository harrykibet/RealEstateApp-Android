plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(project(":core:architecture"))
    implementation(libs.konsist)
    testImplementation(libs.junit.junit)
}
