package com.estatia.realestate.apps.core.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class ContractProcessorTest {

    @Test
    fun `LAW-008 Interface implementation required for Repository`() {
        val source = SourceFile.kotlin(
            "TestRepository.kt",
            """
            package com.estatia.realestate.apps.core.data.repository
            import com.estatia.realestate.apps.core.common.annotations.Repository
            
            @Repository
            class TestRepository { // Missing interface
                fun doWork() {}
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            source,
            providers = listOf(ContractProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Architecture Violation (LAW-008)"))
    }

    @Test
    fun `LAW-008 UseCase returns primitive Result issues warning`() {
        val source = SourceFile.kotlin(
            "TestUseCase.kt",
            """
            package com.estatia.realestate.apps.core.domain.usecase
            import com.estatia.realestate.apps.core.common.annotations.UseCase
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
            providers = listOf(ContractProcessorProvider())
        )
        // Warnings don't fail compilation
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        assertTrue(result.messages.contains("Domain Smell"))
        assertTrue(result.messages.contains("returns 'AppResult<kotlin.Boolean>'"))
    }
}
