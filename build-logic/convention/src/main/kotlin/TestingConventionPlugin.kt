import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class TestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            dependencies {
                // Common unit test dependencies
                listOf(
                    lib.findLibrary("junit.junit").get(),
                    lib.findLibrary("junit.jupiter").get(),
                    lib.findLibrary("kotest.runner.junit5").get(),
                    lib.findLibrary("mockk").get(),
                    lib.findLibrary("core.testing").get(),
                    lib.findLibrary("androidx.test.core.ktx").get(),
                    lib.findLibrary("hilt.android.testing").get(),
                    lib.findLibrary("leakcanary.android.instrumentation").get()
                ).forEach { add("testImplementation", it) }

                // Android test dependencies
                listOf(
                    lib.findLibrary("uiautomator").get(),
                    lib.findLibrary("espresso.intents").get(),
                    lib.findLibrary("espresso.contrib").get(),
                    lib.findLibrary("core.testing").get(),
                    lib.findLibrary("androidx.test.ext.junit").get(),
                    lib.findLibrary("espresso.core").get()
                ).forEach { add("androidTestImplementation", it) }
            }
        }
    }
}
