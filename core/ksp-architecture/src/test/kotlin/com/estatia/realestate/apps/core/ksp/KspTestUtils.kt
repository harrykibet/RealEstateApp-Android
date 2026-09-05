package com.estatia.realestate.apps.core.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import com.google.devtools.ksp.processing.SymbolProcessorProvider

object KspTestUtils {

    val annotationsSource = SourceFile.kotlin(
        "ArchitectureAnnotations.kt",
        """
        package com.estatia.realestate.apps.core.common.annotations
        annotation class Repository
        annotation class Service
        annotation class UseCase
        annotation class ViewModelMarker
        annotation class UiState
        """.trimIndent()
    )

    val resultSource = SourceFile.kotlin(
        "AppResult.kt",
        """
        package com.estatia.realestate.apps.core.common.exceptions
        sealed class AppResult<out T>
        """.trimIndent()
    )

    val coroutineStubs = SourceFile.kotlin(
        "CoroutineStubs.kt",
        """
        package kotlinx.coroutines.flow
        interface Flow<out T>
        interface StateFlow<out T> : Flow<T>
        interface MutableStateFlow<T> : StateFlow<T>
        """.trimIndent()
    )

    val firebaseStub = SourceFile.kotlin(
        "FirebaseStubs.kt",
        """
        package com.google.firebase.auth
        class FirebaseUser
        """.trimIndent()
    )

    fun compile(
        vararg source: SourceFile,
        providers: List<SymbolProcessorProvider>
    ): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            sources = source.toList()
            symbolProcessorProviders = providers
            inheritClassPath = true
            messageOutputStream = System.out
            kotlincArguments = listOf("-Xskip-metadata-version-check")
        }.compile()
    }
}
