package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class Law015_DirectSystemTimeTestTest {

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
                ).to("src/test/kotlin/com/estatia/realestate/apps/MyTest.kt")
            )
            .issues(Law015_DirectSystemTimeTestDetector.ISSUE)
            .run()
            .expectContains("Tests should not depend on real time")
    }
}
