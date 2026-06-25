import com.android.build.gradle.DynamicFeaturePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import com.android.build.api.dsl.DynamicFeatureExtension

class DynamicFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(DynamicFeaturePlugin::class)

        extensions.configure<DynamicFeatureExtension> {
            // Optional: shared configurations like compileSdk, etc.
            // You can also delegate common configs to a shared config method.
        }
    }
}
