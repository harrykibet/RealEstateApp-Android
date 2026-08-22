import com.estatia.realestate.apps.configurePackagingOptions
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidPackagingConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.withId("com.android.application") {
            configurePackagingOptions(project)
        }
        project.plugins.withId("com.android.library") {
            configurePackagingOptions(project)
        }
        project.plugins.withId("com.android.test") {
            configurePackagingOptions(project)
        }
    }
}
