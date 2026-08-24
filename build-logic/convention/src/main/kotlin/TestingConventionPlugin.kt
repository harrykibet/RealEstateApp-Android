import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import com.estatia.realestate.apps.libs

class TestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            dependencies {
                "implementation"(libs.findLibrary("androidx.tracing").get())
                
                // Add our centralized test platform via testFixtures
                // We use the string notation or the project notation directly
                add("testImplementation", testFixtures(project(":core:testing")))
                add("androidTestImplementation", testFixtures(project(":core:testing")))

                // Common unit test dependencies
                listOf(
                    libs.findLibrary("junit.junit").get(),
                    libs.findLibrary("junit.jupiter").get(),
                    libs.findLibrary("junit.vintage.engine").get(),
                    libs.findLibrary("kotest.runner.junit5").get(),
                    libs.findLibrary("mockk").get(),
                    libs.findLibrary("turbine").get(),
                    libs.findLibrary("kotlinx-coroutines-test").get(),
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
                    libs.findLibrary("espresso.core").get(),
                    libs.findLibrary("hilt.android.testing").get(),
                    libs.findLibrary("androidx.compose.ui.test.junit4").get()
                ).forEach { add("androidTestImplementation", it) }
                
                "debugImplementation"(libs.findLibrary("androidx.compose.ui.test.manifest").get())
            }
        }
    }
}
