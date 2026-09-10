package com.estatia.realestate.apps.core.ksp_architecture

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class Law008_AbstractionLeakageProcessorTest {

    @Test
    fun `LAW-008 Abstraction leakage fails compilation`() {
        val source = SourceFile.kotlin(
            "TestRepository.kt",
            """
            package com.estatia.realestate.apps.core.data.repositories
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
            providers = listOf(Law008_AbstractionLeakageProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("LAW-008"))
    }
}
