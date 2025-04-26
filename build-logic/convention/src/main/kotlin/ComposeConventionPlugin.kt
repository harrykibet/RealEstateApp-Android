import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<BaseExtension> {
                buildFeatures.apply {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = "1.5.10"
                }
            }

            dependencies {
                val bom = lib.findLibrary("androidx-compose-bom").get()
                "implementation"(platform(bom))
                "implementation"(lib.findLibrary("androidx-compose-ui").get())
                "implementation"(lib.findLibrary("androidx-compose-material3").get())
                "implementation"(lib.findLibrary("androidx-navigation-compose").get())
                "implementation"(lib.findLibrary("androidx-compose-ui-tooling-preview").get())
                "debugImplementation"(lib.findLibrary("androidx-compose-ui-tooling").get())
                "implementation"(lib.findLibrary("material-icons-extended").get())
            }
        }
    }
}
