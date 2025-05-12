import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import utils.libs

class TestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            dependencies {
                // Common unit test dependencies
                listOf(
                    libs.findLibrary("junit.junit").get(),
                    libs.findLibrary("junit.jupiter").get(),
                    libs.findLibrary("kotest.runner.junit5").get(),
                    libs.findLibrary("mockk").get(),
                    libs.findLibrary("core.testing").get(),
                    libs.findLibrary("androidx.test.core.ktx").get(),
                    libs.findLibrary("hilt.android.testing").get(),
                    libs.findLibrary("leakcanary.android.instrumentation").get()
                ).forEach { add("testImplementation", it) }

                // Android test dependencies
                listOf(
                    libs.findLibrary("uiautomator").get(),
                    libs.findLibrary("espresso.intents").get(),
                    libs.findLibrary("espresso.contrib").get(),
                    libs.findLibrary("core.testing").get(),
                    libs.findLibrary("androidx.test.ext.junit").get(),
                    libs.findLibrary("espresso.core").get()
                ).forEach { add("androidTestImplementation", it) }
            }
        }
    }
}
