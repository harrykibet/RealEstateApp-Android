import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import com.estatia.realestate.apps.libs

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {

        pluginManager.apply("com.estatia.realestate.apps.android.config")
        pluginManager.apply("com.estatia.realestate.apps.android.testing")
        pluginManager.apply("com.estatia.realestate.apps.android.compose")
        pluginManager.apply("com.estatia.realestate.apps.android.flavors")
        pluginManager.apply("com.estatia.realestate.apps.android.hilt")
        pluginManager.apply("androidx.navigation.safeargs.kotlin")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
        pluginManager.apply("com.estatia.realestate.apps.android.jacoco")


        dependencies {
            "implementation"(project(":core:ui"))
            "implementation"(project(":core:common"))
            "implementation"(project(":core:domain"))
            "implementation"(project(":core:navigation"))
            "implementation"(project(":core:model"))
            "implementation"(project(":core:design-system"))
            "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
        }
    }
}
