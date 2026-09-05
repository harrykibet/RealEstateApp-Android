package com.estatia.realestate.apps.core.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class ResultWrappingProcessorTest {

    @Test
    fun `LAW-009 Result wrapping violation fails compilation`() {
        val source = SourceFile.kotlin(
            "TestRepository.kt",
            """
            package com.estatia.realestate.apps.core.data.repository
            import com.estatia.realestate.apps.core.common.annotations.Repository
            
            interface ITestRepository
            
            @Repository
            class TestRepository : ITestRepository {
                fun getData(): String = "Data"
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.resultSource, 
            source,
            providers = listOf(ResultWrappingProcessorProvider())
        )
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
            import kotlinx.coroutines.flow.Flow
            
            interface ITestRepository
            
            @Repository
            class TestRepository : ITestRepository {
                fun loadData(): AppResult<String> = TODO()
                fun streamData(): Flow<String> = TODO()
                fun doWork() {}
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.resultSource, 
            KspTestUtils.coroutineStubs, 
            source,
            providers = listOf(ResultWrappingProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}
