package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class DirectSystemTimeDetectorTest {

    @Test
    fun `currentTimeMillis in production reports error`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun now() = System.currentTimeMillis()
                    }
                    """.trimIndent()
                ).to("src/com/estatia/realestate/apps/Test.kt")
            )
            .issues(DirectSystemTimeDetector.ISSUE)
            .run()
            .expectContains("Direct usage of system time is forbidden in production")
    }

    @Test
    fun `currentTimeMillis in test reports warning`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class MyTest {
                        fun now() = System.currentTimeMillis()
                    }
                    """.trimIndent()
                ).to("src/test/com/estatia/realestate/apps/MyTest.kt")
            )
            .issues(DirectSystemTimeDetector.ISSUE)
            .run()
            .expectContains("Tests should not depend on real time")
    }
}
