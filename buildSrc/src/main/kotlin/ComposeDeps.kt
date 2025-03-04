
@file:Suppress("ConstPropertyName")
object ComposeDeps {
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    const val composeUi = "androidx.compose.ui:ui"
    const val composeCompiler = "androidx.compose.compiler:compiler:${Versions.composeCompiler}"
    const val composeMaterial = "androidx.compose.material:material"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling"
    const val composeRuntimeLiveData = "androidx.compose.runtime:runtime-livedata:${Versions.composeRuntime}"
    const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
    const val composeMaterial3 = "androidx.compose.material3:material3:${Versions.composeMaterial3}"

    // ComposeBom not included, add it manually in the dependencies block
    val AllComposeDeps = listOf(
        composeUi,
        composeCompiler,
        composeMaterial,
        composeUiToolingPreview,
        composeRuntimeLiveData,
        activityCompose,
        composeMaterial3
    )
    val ComposeDebugDeps = listOf ( composeUiTooling )
}