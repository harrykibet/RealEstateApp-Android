package com.estatia.realestate.apps.core.ksp_architecture

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import com.google.devtools.ksp.processing.SymbolProcessorProvider

object KspTestUtils {

    val annotationsSource = SourceFile.kotlin(
        "ArchitectureAnnotations.kt",
        """
        package com.estatia.realestate.apps.core.architecture.annotations
        
        object Identity {
            annotation class Repository
            annotation class Service
            annotation class UseCase
            annotation class ViewModelMarker
            annotation class Coordinator
            annotation class Manager
            annotation class DataSource
            annotation class Contract
            annotation class AppEntryPoint
        }
        
        object Logic {
            annotation class Utility
            annotation class Mapper
            annotation class Policy
            annotation class Foundation
            annotation class Helper
            annotation class ErrorMapper
        }
        
        object Data {
            annotation class DomainModel
            annotation class EntityModel
            annotation class UiState
            annotation class BatteryState
            annotation class NetworkState
            annotation class EnvironmentState
            annotation class AnalyticsState
            annotation class AuthState
            annotation class PlayerState
            annotation class UiAction
            annotation class UiEvent
        }
        
        object Ui {
            annotation class UiScreen
            annotation class UiComponent
            annotation class UiPrimitiveFunction
            annotation class UiRoute
            annotation class UiPrimitive
        }
        
        object Safety {
            annotation class AllowedArchitectureDependency(val reason: String)
        }
        """.trimIndent()
    )

    val resultSource = SourceFile.kotlin(
        "AppResult.kt",
        """
        package com.estatia.realestate.apps.core.common.exceptions
        sealed class AppResult<out T>
        class AppException(m: String) : Exception(m)
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

    val lifecycleStubs = SourceFile.kotlin(
        "LifecycleStubs.kt",
        """
        package androidx.lifecycle
        abstract class ViewModel
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
