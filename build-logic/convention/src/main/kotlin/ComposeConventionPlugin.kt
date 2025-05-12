import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import utils.libs

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
                val bom = libs.findLibrary("androidx-compose-bom").get()
                "implementation"(platform(bom))
                "implementation"(libs.findLibrary("androidx-compose-ui").get())
                "implementation"(libs.findLibrary("androidx-compose-material3").get())
                "implementation"(libs.findLibrary("androidx-navigation-compose").get())
                "implementation"(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
                "implementation"(libs.findLibrary("material-icons-extended").get())
                "implementation"(libs.findLibrary("androidx-compose-foundation").get())
                "implementation"(libs.findLibrary("androidx-compose-runtime").get())
                "implementation"(libs.findLibrary("coil-compose").get())
            }
        }
    }
}
