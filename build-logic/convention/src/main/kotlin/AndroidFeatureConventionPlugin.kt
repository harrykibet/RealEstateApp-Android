import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {

        pluginManager.apply("com.application.real_estate_app.android.config")
        pluginManager.apply("com.application.real_estate_app.android.testing")
        pluginManager.apply("com.application.real_estate_app.android.compose")
        pluginManager.apply("com.application.real_estate_app.hilt")
        pluginManager.apply("androidx.navigation.safeargs.kotlin")

        dependencies {
            "implementation"(project(":core:ui"))
            "implementation"(project(":core:common"))
            "implementation"(project(":core:data"))
        }
    }
}
