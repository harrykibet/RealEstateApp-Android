package com.estatia.realestate.apps.core.ksp_architecture

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class Law002_ExposedMutableStateProcessorTest {

    @Test
    fun `LAW-002 ViewModel exposing MutableStateFlow fails compilation`() {
        val source = SourceFile.kotlin(
            "TestViewModel.kt",
            """
            package com.estatia.realestate.apps.feature.test
            import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
            import kotlinx.coroutines.flow.MutableStateFlow
            
            @ViewModelMarker
            class TestViewModel {
                // Explicit type to ensure KSP can resolve it even if the initializer is invalid in stub
                val mutableState: MutableStateFlow<Int> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(Law002_ExposedMutableStateProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Architecture Law (LAW-002)"))
        assertTrue(result.messages.contains("exposes mutable state"))
    }
}
