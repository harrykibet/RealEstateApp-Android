import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import utils.libs

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {

        pluginManager.apply("com.application.real_estate_app.android.config")
        pluginManager.apply("com.application.real_estate_app.android.testing")
        pluginManager.apply("com.application.real_estate_app.android.compose")
        pluginManager.apply("com.application.real_estate_app.hilt")
        pluginManager.apply("androidx.navigation.safeargs.kotlin")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")


        dependencies {
            "implementation"(project(":core:ui"))
            "implementation"(project(":core:common"))
            "implementation"(project(":core:data"))
            "implementation"(project(":core:design-system"))
            "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
        }
    }
}
