package com.estatia.realestate.apps.core.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class ArchitectureProcessorTest {

    private val annotationsSource = SourceFile.kotlin(
        "ArchitectureAnnotations.kt",
        """
        package com.estatia.realestate.apps.core.common.annotations
        annotation class Repository
        annotation class Service
        annotation class UseCase
        annotation class ViewModelMarker
        """.trimIndent()
    )

    private val resultSource = SourceFile.kotlin(
        "AppResult.kt",
        """
        package com.estatia.realestate.apps.core.common.exceptions
        sealed class AppResult<out T>
        """.trimIndent()
    )

    @Test
    fun `LAW-009 Result wrapping violation fails compilation`() {
        val source = SourceFile.kotlin(
            "TestRepository.kt",
            """
            package com.estatia.realestate.apps.core.data.repository
            import com.estatia.realestate.apps.core.common.annotations.Repository
            
            @Repository
            class TestRepository {
                fun getData(): String = "Data"
            }
            """.trimIndent()
        )

        val result = compile(annotationsSource, resultSource, source)
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Architecture Violation (LAW-009)"))
    }

    @Test
    fun `LAW-009 Result wrapping success passes compilation`() {
        val source = SourceFile.kotlin(
            "TestRepository.kt",
            """
            package com.estatia.realestate.apps.core.data.repository
            import com.estatia.realestate.apps.core.common.annotations.Repository
            import com.estatia.realestate.apps.core.common.exceptions.AppResult
            
            @Repository
            class TestRepository {
                fun loadData(): AppResult<String> = TODO()
                fun doWork() {}
            }
            """.trimIndent()
        )

        val result = compile(annotationsSource, resultSource, source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `LAW-008 Abstraction leakage fails compilation`() {
        val source = SourceFile.kotlin(
            "TestRepository.kt",
            """
            package com.estatia.realestate.apps.core.data.repository
            import com.estatia.realestate.apps.core.common.annotations.Repository
            import com.estatia.realestate.apps.core.common.exceptions.AppResult
            
            @Repository
            class TestRepository {
                fun getUser(): AppResult<com.google.firebase.auth.FirebaseUser> = TODO()
            }
            """.trimIndent()
        )

        val result = compile(annotationsSource, resultSource, source)
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Architecture Violation (LAW-008)"))
    }

    @Test
    fun `LAW-016 ViewModel mutable state fails compilation`() {
        val source = SourceFile.kotlin(
            "TestViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
            
            class MutableStateFlow<T>(val value: T)
            
            @ViewModelMarker
            class TestViewModel {
                val state = MutableStateFlow(0)
            }
            """.trimIndent()
        )

        val result = compile(annotationsSource, resultSource, source)
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Architecture Violation (LAW-016)"))
    }

    private fun compile(vararg source: SourceFile): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            sources = source.toList()
            symbolProcessorProviders = listOf(ArchitectureProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
            kotlincArguments = listOf("-Xskip-metadata-version-check")
        }.compile()
    }
}
