import org.gradle.api.Plugin
import org.gradle.api.Project


class AndroidCoreConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.estatia.realestate.apps.android.config")
        pluginManager.apply("com.estatia.realestate.apps.android.testing")
        pluginManager.apply("com.estatia.realestate.apps.hilt")
        pluginManager.apply("com.estatia.realestate.apps.android.flavors")
    }
}
