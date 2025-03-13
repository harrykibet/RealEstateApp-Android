import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidTestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {

                // Common unit test dependencies
                listOf(
                    lib.findLibrary("junit.junit"),
                    lib.findLibrary("junit.jupiter"),
                    lib.findLibrary("kotest.runner.junit5"),
                    lib.findLibrary("mockk"),
                    lib.findLibrary("core.testing"),
                    lib.findLibrary("androidx.test.core.ktx"),
                    lib.findLibrary("hilt.android.testing"),
                    lib.findLibrary("leakcanary.android.instrumentation")
                ).forEach { dependency ->
                    add("testImplementation", dependency)
                }

                // Android test dependencies
                listOf(
                    lib.findLibrary("uiautomator"),
                    lib.findLibrary("espresso.intents"),
                    lib.findLibrary("espresso.contrib"),
                    lib.findLibrary("core.testing"),
                    lib.findLibrary("androidx.test.ext.junit"),
                    lib.findLibrary("espresso.core")
                ).forEach { dependency ->
                    add("androidTestImplementation", dependency)
                }
            }
        }
    }
}
