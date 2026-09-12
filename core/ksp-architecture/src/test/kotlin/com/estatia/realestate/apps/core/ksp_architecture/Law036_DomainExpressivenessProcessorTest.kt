package com.estatia.realestate.apps.core.ksp_architecture

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class Law036_DomainExpressivenessProcessorTest {

    @Test
    fun `LAW-036 UseCase returns primitive Result issues warning`() {
        val source = SourceFile.kotlin(
            "TestUseCase.kt",
            """
            package com.estatia.realestate.apps.core.domain.usecase
            import com.estatia.realestate.apps.core.architecture.annotations.Identity.UseCase
            import com.estatia.realestate.apps.core.common.exceptions.AppResult
            
            interface ITestUseCase
            
            @UseCase
            class TestUseCase : ITestUseCase {
                fun validate(): AppResult<Boolean> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.resultSource, 
            source,
            providers = listOf(Law036_DomainExpressivenessProcessorProvider())
        )
        // Rule is now a CONVENTION (Warning), so compilation should pass
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        assertTrue(result.messages.contains("LAW-036"))
        assertTrue(result.messages.contains("Consider using a sealed class or enum"))
    }
}
