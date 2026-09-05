package com.estatia.realestate.apps.core.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class ConstructorAbstractionProcessorTest {

    @Test
    fun `LAW-030 Constructor must only accept abstractions`() {
        val source = SourceFile.kotlin(
            "TestUseCase.kt",
            """
            package com.estatia.realestate.apps.core.domain.usecase
            import com.estatia.realestate.apps.core.common.annotations.UseCase
            
            interface IUseCase
            class ConcreteDatabase
            
            @UseCase
            class TestUseCase(val db: ConcreteDatabase) : IUseCase {
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            source,
            providers = listOf(ConstructorAbstractionProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Architecture Violation (LAW-030)"))
    }

    @Test
    fun `LAW-030 Constructor accepts interface and passes`() {
        val source = SourceFile.kotlin(
            "TestUseCase.kt",
            """
            package com.estatia.realestate.apps.core.domain.usecase
            import com.estatia.realestate.apps.core.common.annotations.UseCase
            
            interface IUseCase
            interface IDatabase
            
            @UseCase
            class TestUseCase(val db: IDatabase) : IUseCase {
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            source,
            providers = listOf(ConstructorAbstractionProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}
