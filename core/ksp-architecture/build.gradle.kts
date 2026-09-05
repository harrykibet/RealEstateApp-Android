import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(libs.symbol.processing.api)

    testImplementation(libs.junit.junit)
    testImplementation(libs.kotlinCompileTesting)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xopt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
    }
}
