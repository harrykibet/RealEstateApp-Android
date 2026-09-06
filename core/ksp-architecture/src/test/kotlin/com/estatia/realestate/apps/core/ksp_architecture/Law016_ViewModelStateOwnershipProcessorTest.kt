package com.estatia.realestate.apps.core.ksp_architecture

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class Law016_ViewModelStateOwnershipProcessorTest {

    @Test
    fun `LAW-016 ViewModel mutable state fails compilation`() {
        val mutableStateFlowStub = SourceFile.kotlin(
            "MutableStateFlow.kt",
            """
            package kotlinx.coroutines.flow
            class MutableStateFlow<T>(val value: T)
            """.trimIndent()
        )

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
            mutableStateFlowStub,
            source,
            providers = listOf(Law016_ViewModelStateOwnershipProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Architecture Violation (LAW-016)"))
    }
}
