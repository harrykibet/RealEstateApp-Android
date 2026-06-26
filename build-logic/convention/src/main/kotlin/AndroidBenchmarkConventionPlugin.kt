/*import com.android.build.api.dsl.TestExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidBenchmarkConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.test")

        // SDK versions and test runner only — no buildTypes, no targetProjectPath
        // Those are module-specific and belong in each module's build.gradle.kts
        extensions.configure<TestExtension> {
            compileSdk = 36
            defaultConfig {
                minSdk = 28
                targetSdk = 36
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
            experimentalProperties["android.experimental.self-instrumenting"] = true
        }

        pluginManager.withPlugin("org.jetbrains.kotlin.android") {
            extensions.configure<KotlinAndroidProjectExtension> {
                jvmToolchain(17)
            }
        }
    }
}*/