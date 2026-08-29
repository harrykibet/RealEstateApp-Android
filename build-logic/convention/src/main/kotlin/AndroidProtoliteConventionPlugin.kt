import com.estatia.realestate.apps.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.exclude

/**
 * Firebase/protolite compatibility policy.
 *
 * Applied only to modules whose dependency graph contains the
 * Firebase libraries requiring protobuf-javalite.
 *
 * Do not apply to modules using libraries requiring full
 * protobuf-java APIs (e.g. OTLP exporters).
 */
class AndroidProtoliteConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configurations.all {
                // Resolve Protobuf duplicate class conflict
                exclude(group = "com.google.protobuf", module = "protobuf-lite")
                exclude(group = "com.google.protobuf", module = "protobuf-java")
                exclude(group = "com.google.firebase", module = "protolite-well-known-types")
                resolutionStrategy {
                    force(libs.findLibrary("protobuf.javalite").get())
                }
            }
        }
    }
}
