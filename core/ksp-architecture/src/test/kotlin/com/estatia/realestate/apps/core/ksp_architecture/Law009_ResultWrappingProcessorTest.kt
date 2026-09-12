package com.estatia.realestate.apps.core.ksp_architecture

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class Law009_ResultWrappingProcessorTest {

    @Test
    fun `LAW-009 Result wrapping violation issues warning`() {
        val source = SourceFile.kotlin(
            "TestRepository.kt",
            """
            package com.estatia.realestate.apps.core.data.repositories
            import com.estatia.realestate.apps.core.architecture.annotations.Identity.Repository
            import java.util.List
            
            interface ITestRepository
            
            @Repository
            class TestRepository : ITestRepository {
                // Non-trivial complex type requires wrapping
                fun getData(): List<String> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.resultSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(Law009_ResultWrappingProcessorProvider())
        )
        // Rule is now a CONVENTION (Warning), so compilation should pass
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        assertTrue(result.messages.contains("LAW-009"))
    }

    @Test
    fun `LAW-009 Trivial getter returning simple type is exempt`() {
        val source = SourceFile.kotlin(
            "TestRepository.kt",
            """
            package com.estatia.realestate.apps.core.data.repositories
            import com.estatia.realestate.apps.core.architecture.annotations.Identity.Repository
            
            interface ITestRepository
            
            @Repository
            class TestRepository : ITestRepository {
                fun getId(): String = "123"
                fun isActive(): Boolean = true
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.resultSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(Law009_ResultWrappingProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        // Should NOT contain warning because they are exempt simple types
        assertTrue(!result.messages.contains("LAW-009"))
    }

    @Test
    fun `LAW-009 Standard wrappers pass`() {
        val source = SourceFile.kotlin(
            "TestRepository.kt",
            """
            package com.estatia.realestate.apps.core.data.repositories
            import com.estatia.realestate.apps.core.architecture.annotations.Identity.Repository
            import com.estatia.realestate.apps.core.common.exceptions.AppResult
            import kotlinx.coroutines.flow.Flow
            
            interface ITestRepository
            
            @Repository
            class TestRepository : ITestRepository {
                fun loadData(): AppResult<String> = TODO()
                fun streamData(): Flow<String> = TODO()
                fun doWork() {} // Unit is allowed
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.resultSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(Law009_ResultWrappingProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}
