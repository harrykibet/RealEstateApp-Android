package com.estatia.realestate.apps.core.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class AbstractionBoundaryProcessorTest {

    @Test
    fun `LAW-008 Abstraction leakage fails compilation`() {
        val source = SourceFile.kotlin(
            "TestRepository.kt",
            """
            package com.estatia.realestate.apps.core.data.repository
            import com.estatia.realestate.apps.core.common.annotations.Repository
            import com.estatia.realestate.apps.core.common.exceptions.AppResult
            import com.google.firebase.auth.FirebaseUser
            
            interface ITestRepository
            
            @Repository
            class TestRepository : ITestRepository {
                fun getUser(): AppResult<FirebaseUser> = TODO()
            }
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            KspTestUtils.resultSource, 
            KspTestUtils.firebaseStub, 
            source,
            providers = listOf(AbstractionBoundaryProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Architecture Violation (LAW-008)"))
        assertTrue(result.messages.contains("exposes infrastructure type: com.google.firebase.auth.FirebaseUser"))
    }
}
