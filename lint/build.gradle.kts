plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.lint.api)
    compileOnly(libs.android.lint.checks)

    testImplementation(libs.android.lint.api)
    testImplementation(libs.android.lint.checks)
    testImplementation(libs.android.lint.tests)
    testImplementation(libs.junit.junit)
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    manifest {
        attributes("Lint-Registry-v2" to "com.estatia.realestate.apps.lint.registry.EstatiaIssueRegistry")
    }
}
