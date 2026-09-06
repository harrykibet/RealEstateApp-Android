package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class Law007_DirectSystemTimeProdTest {

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
                ).to("src/main/kotlin/com/estatia/realestate/apps/Test.kt")
            )
            .issues(Law007_DirectSystemTimeProdDetector.ISSUE)
            .run()
            .expectContains("Direct usage of system time is forbidden in production")
    }

    @Test
    fun `usage in TimeProvider is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class RealTimeProvider {
                        fun now() = System.currentTimeMillis()
                    }
                    """.trimIndent()
                ).to("src/main/kotlin/com/estatia/realestate/apps/RealTimeProvider.kt")
            )
            .issues(Law007_DirectSystemTimeProdDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `usage in test files is ignored by prod detector`() {
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
                ).to("src/test/kotlin/com/estatia/realestate/apps/MyTest.kt")
            )
            .issues(Law007_DirectSystemTimeProdDetector.ISSUE)
            .run()
            .expectClean()
    }
}
