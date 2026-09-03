package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class ComplexityBudgetDetectorTest {

    @Test
    fun `method with many parameters reports warning`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun tooMany(p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {}
                    }
                    """.trimIndent()
                )
            )
            .issues(ComplexityBudgetDetector.PARAMETER_COUNT_WARNING)
            .run()
            .expectContains("Method 'tooMany' has many parameters (8)")
    }

    @Test
    fun `constructor with many dependencies reports error`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Orchestrator(
                        d1: Int, d2: Int, d3: Int, d4: Int, d5: Int, 
                        d6: Int, d7: Int, d8: Int, d9: Int, d10: Int
                    )
                    """.trimIndent()
                )
            )
            .issues(ComplexityBudgetDetector.CONSTRUCTOR_DEPENDENCY_ERROR)
            .run()
            .expectContains("Constructor of 'Orchestrator' has too many dependencies (10)")
    }

    @Test
    fun `massive method reports fatal`() {
        val lines = (1..301).joinToString("\n") { "println($it)" }
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun massive() {
                            $lines
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(ComplexityBudgetDetector.METHOD_SIZE_FATAL)
            .run()
            .expectContains("Method 'massive' is too long")
    }
}
