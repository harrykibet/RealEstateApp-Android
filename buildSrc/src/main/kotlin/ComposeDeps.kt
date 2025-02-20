
@file:Suppress("ConstPropertyName")
object ComposeDeps {
    const val composeBom = "androidx.compose:compose-bom:${Versions.androidx_compose_bom}"
    const val composeUi = "androidx.compose.ui:ui"
    const val composeCompiler = "androidx.compose.compiler:compiler:${Versions.androidx_compose_compiler}"
    const val composeMaterial = "androidx.compose.material:material"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling"
    const val composeRuntimeLiveData = "androidx.compose.runtime:runtime-livedata:${Versions.androidx_compose_runtime}"
    const val activityCompose = "androidx.activity:activity-compose:${Versions.androidx_activity_compose}"
    const val composeMaterial3 = "androidx.compose.material3:material3:${Versions.androidx_compose_material3}"

    // ComposeBom not included, add it manually in the dependencies block
    val AllComposeDependencies = listOf(
        composeUi,
        composeMaterial,
        composeUiToolingPreview,
        composeRuntimeLiveData,
        activityCompose,
        composeMaterial3
    )
    val ComposeDebugDependencies = listOf ( composeUiTooling )
}