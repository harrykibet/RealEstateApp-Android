import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(target){

            dependencies {
                "implementation"(platform(lib.findLibrary("androidx.compose.bom").get()))
                "implementation"(lib.findLibrary("androidx.compose.ui").get())
                "implementation"(lib.findLibrary("androidx.compose.material3").get())
                "implementation"(lib.findLibrary("androidx.navigation.compose").get())
                "implementation"(lib.findLibrary("androidx.compose.ui.tooling.preview").get())
                "debugImplementation"(lib.findLibrary("androidx.compose.ui.tooling").get())
            }
        }
    }
}
