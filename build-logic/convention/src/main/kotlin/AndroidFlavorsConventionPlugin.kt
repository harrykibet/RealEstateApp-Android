import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.estatia.realestate.apps.configureFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidFlavorsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {

        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> {
                configureFlavors(this)
            }
        }

        pluginManager.withPlugin("com.android.library") {
            extensions.configure<LibraryExtension> {
                configureFlavors(this)
            }
        }

    }
}