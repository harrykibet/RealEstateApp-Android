package com.estatia.realestate.apps.core.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class ViewModelProcessorTest {

    @Test
    fun `LAW-018 ViewModel multiple public StateFlows fails compilation`() {
        val source = SourceFile.kotlin(
            "TestViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
            import kotlinx.coroutines.flow.StateFlow
            
            @ViewModelMarker
            class TestViewModel {
                val state1: StateFlow<Int> = TODO()
                val state2: StateFlow<String> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(ViewModelProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Architecture Violation (LAW-018)"))
    }

    @Test
    fun `LAW-016 ViewModel mutable state fails compilation`() {
        val source = SourceFile.kotlin(
            "TestViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
            import kotlinx.coroutines.flow.MutableStateFlow
            
            @ViewModelMarker
            class TestViewModel {
                val state: MutableStateFlow<Int> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(ViewModelProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Architecture Violation (LAW-016)"))
    }

    @Test
    fun `LAW-018 ViewModel with single StateFlow passes`() {
        val source = SourceFile.kotlin(
            "TestViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
            import kotlinx.coroutines.flow.StateFlow
            
            @ViewModelMarker
            class TestViewModel {
                val uiState: StateFlow<Int> = TODO()
                private val _internalState: String = ""
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(ViewModelProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}
