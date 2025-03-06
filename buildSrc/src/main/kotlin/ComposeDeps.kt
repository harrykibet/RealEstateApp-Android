/**
 * Object containing dependencies related to Jetpack Compose.
 *
 * This object provides a centralized location to manage all Compose-related
 * dependencies, including the BOM (Bill of Materials), UI components, tooling,
 * compiler, and more. It simplifies dependency management by providing
 * easy-to-use properties and functions for retrieving dependency artifacts.
 *
 * @property composeBom The Compose BOM (Bill of Materials) dependency. This is
 *                     used to manage the versions of other Compose dependencies.
 * @property composeUi The core Compose UI dependency.
 * @property composeMaterial The Compose Material Design components dependency.
 * @property composeUiToolingPreview The Compose UI tooling preview dependency for
 *                                  previewing composables in Android Studio.
 * @property composeUiTooling The Compose UI tooling dependency for development and debugging.
 * @property composeCompiler The Compose compiler dependency.
 * @property composeRuntimeLiveData The Compose runtime LiveData integration dependency.
 * @property activityCompose The Compose integration with the Activity library.
 * @property composeMaterial3 The Compose Material 3 dependency.
 */
@Suppress("MemberVisibilityCanBePrivate")
object ComposeDeps {
    val composeBom = Dependency.BomDependency(
        group = "androidx.compose",
        name = "compose-bom",
        version = Versions.composeBom
    ).toGradleNotation

    val composeUi = Dependency.BomManagedDependency(
        group = "androidx.compose.ui",
        name = "ui"
    ).toGradleNotation

    val composeMaterial = Dependency.BomManagedDependency(
        group = "androidx.compose.material",
        name = "material"
    ).toGradleNotation

    val composeUiToolingPreview = Dependency.BomManagedDependency(
        group = "androidx.compose.ui",
        name = "ui-tooling-preview"
    ).toGradleNotation

    val composeUiTooling = Dependency.BomManagedDependency(
        group = "androidx.compose.ui",
        name = "ui-tooling"
    ).toGradleNotation

    val composeCompiler = Dependency.VersionedDependency(
        group = "androidx.compose.compiler",
        name = "compiler",
        version = Versions.composeCompiler
    ).toGradleNotation

    val composeRuntimeLiveData = Dependency.VersionedDependency(
        group = "androidx.compose.runtime",
        name = "runtime-livedata",
        version = Versions.composeRuntime
    ).toGradleNotation

    val activityCompose = Dependency.VersionedDependency(
        group = "androidx.activity",
        name = "activity-compose",
        version = Versions.activityCompose
    ).toGradleNotation

    val composeMaterial3 = Dependency.VersionedDependency(
        group = "androidx.compose.material3",
        name = "material3",
        version = Versions.composeMaterial3
    ).toGradleNotation

    fun getAllComposeDeps() = listOf(
        composeUi,
        composeCompiler,
        composeMaterial,
        composeUiToolingPreview,
        composeRuntimeLiveData,
        activityCompose,
        composeMaterial3
    )


    fun getComposeDebugDeps() = listOf(
        composeUiTooling
    )
}
