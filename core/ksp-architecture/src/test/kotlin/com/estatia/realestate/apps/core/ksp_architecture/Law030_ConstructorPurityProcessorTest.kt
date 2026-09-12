package com.estatia.realestate.apps.core.ksp_architecture

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class Law030_ConstructorPurityProcessorTest {

    @Test
    fun `LAW-030 Constructor accepts non-abstraction and reports warning`() {
        val source = SourceFile.kotlin(
            "TestUseCase.kt",
            """
            package com.estatia.realestate.apps.core.domain.usecase
            import com.estatia.realestate.apps.core.architecture.annotations.Identity.UseCase
            
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
            providers = listOf(Law030_ConstructorPurityProcessorProvider())
        )
        // [REF] Softened Law Policy: Statistical risks are now WARNINGS
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        assertTrue(result.messages.contains("LAW-030"))
        assertTrue(result.messages.contains("Architecture Warning"))
    }

    @Test
    fun `LAW-030 Constructor accepts interface and passes`() {
        val source = SourceFile.kotlin(
            "TestUseCase.kt",
            """
            package com.estatia.realestate.apps.core.domain.usecase
            import com.estatia.realestate.apps.core.architecture.annotations.Identity.UseCase
            
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
            providers = listOf(Law030_ConstructorPurityProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `LAW-030 Constructor accepts interface without I-prefix and passes`() {
        val source = SourceFile.kotlin(
            "TestUseCase.kt",
            """
            package com.estatia.realestate.apps.core.domain.usecase
            import com.estatia.realestate.apps.core.architecture.annotations.Identity.UseCase
            
            interface UseCaseContract
            interface RepositoryAbstraction
            
            @UseCase
            class TestUseCase(val repo: RepositoryAbstraction) : UseCaseContract
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            source,
            providers = listOf(Law030_ConstructorPurityProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `LAW-030 Constructor accepts explicitly authorized dependency`() {
        val source = SourceFile.kotlin(
            "TestUseCase.kt",
            """
            package com.estatia.realestate.apps.core.domain.usecase
            import com.estatia.realestate.apps.core.architecture.annotations.Identity.UseCase
            import com.estatia.realestate.apps.core.architecture.annotations.Safety.AllowedArchitectureDependency
            
            interface IUseCase
            class ConcreteDatabase
            
            @UseCase
            class TestUseCase(
                @AllowedArchitectureDependency(reason = "Required legacy singleton")
                val db: ConcreteDatabase
            ) : IUseCase
            """.trimIndent()
        )

        val result = KspTestUtils.compile(
            KspTestUtils.annotationsSource, 
            source,
            providers = listOf(Law030_ConstructorPurityProcessorProvider())
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}
