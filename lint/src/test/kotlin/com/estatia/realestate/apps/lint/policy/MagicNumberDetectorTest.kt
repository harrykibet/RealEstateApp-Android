package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class MagicNumberDetectorTest {

    @Test
    fun `magic number in logic reports warning`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun check(age: Int) {
                            if (age > 21) { }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(MagicNumberDetector.ISSUE)
            .run()
            .expectContains("Magic number '21' detected")
    }

    @Test
    fun `named constant is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    const val MIN_AGE = 21
                    class Test {
                        fun check(age: Int) {
                            if (age > MIN_AGE) { }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(MagicNumberDetector.ISSUE)
            .run()
            .expectClean()
    }
}
