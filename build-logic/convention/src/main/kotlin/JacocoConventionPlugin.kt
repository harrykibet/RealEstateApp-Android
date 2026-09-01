import com.android.build.api.variant.AndroidComponentsExtension
import com.estatia.realestate.apps.configureJacoco
import com.estatia.realestate.apps.jacocoThresholds
import org.gradle.api.Plugin
import org.gradle.api.Project

class JacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.withPlugin("com.android.base") {
                val androidComponents = extensions.findByType(AndroidComponentsExtension::class.java)
                if (androidComponents != null) {
                    configureJacoco(androidComponents)

                    // Intelligent Thresholds based on module path
                    val (line, branch) = when {
                        path == ":core:security" || path == ":core:player-engine" -> 0.90 to 0.85
                        path == ":core:network" || path == ":core:database" || path == ":core:domain" -> 0.80 to 0.75
                        path == ":feature:payments" || path == ":feature:auth" -> 0.85 to 0.80
                        path.startsWith(":feature:") -> 0.60 to 0.50
                        path == ":core:model" -> 0.30 to 0.0
                        else -> 0.0 to 0.0
                    }

                    if (line > 0.0 || branch > 0.0) {
                        jacocoThresholds(line, branch)
                    }
                }
            }
        }
    }
}
