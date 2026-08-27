import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import com.estatia.realestate.apps.libs

class TestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            dependencies {
                "implementation"(libs.findLibrary("androidx.tracing").get())
                
                // Add our centralized test platform via testFixtures
                add("testImplementation", testFixtures(project(":core:testing")))
                add("testImplementation", testFixtures(project(":core:testing-network")))
                add("androidTestImplementation", testFixtures(project(":core:testing")))
                add("androidTestImplementation", testFixtures(project(":core:testing-network")))

                // Common unit test dependencies
                listOf(
                    libs.findLibrary("junit.junit").get(),
                    libs.findLibrary("junit.jupiter").get(),
                    libs.findLibrary("mockk").get(),
                    libs.findLibrary("turbine").get(),
                    libs.findLibrary("kotlinx-coroutines-test").get(),
                    libs.findLibrary("core.testing").get(),
                    libs.findLibrary("androidx.test.core.ktx").get()
                ).forEach { add("testImplementation", it) }
                
                add("testRuntimeOnly", libs.findLibrary("junit.vintage.engine").get())

                // Common Android test dependencies
                listOf(
                    libs.findLibrary("androidx.test.ext.junit").get(),
                    libs.findLibrary("core.testing").get()
                ).forEach { add("androidTestImplementation", it) }
            }
        }
    }
}
