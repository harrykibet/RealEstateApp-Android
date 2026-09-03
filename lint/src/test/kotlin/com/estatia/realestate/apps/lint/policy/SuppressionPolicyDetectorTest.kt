package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class SuppressionPolicyDetectorTest {

    @Test
    fun `blind suppression of all reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    @Suppress("all")
                    class Bad
                    """.trimIndent()
                )
            )
            .issues(SuppressionPolicyDetector.ISSUE)
            .run()
            .expectContains("Blind suppression using 'all' is forbidden")
    }

    @Test
    fun `suppression of FATAL rule reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    @Suppress("InfrastructureLeakage")
                    class Bad
                    """.trimIndent()
                )
            )
            .issues(SuppressionPolicyDetector.ISSUE)
            .run()
            .expectContains("cannot be suppressed")
    }

    @Test
    fun `suppression of ERROR rule without justification reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    @Suppress("ExposedMutableState")
                    class Bad
                    """.trimIndent()
                )
            )
            .issues(SuppressionPolicyDetector.ISSUE)
            .run()
            .expectContains("requires a preceding justification comment")
    }

    @Test
    fun `suppression of ERROR rule with justification is clean`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    // Justification: reason
                    @Suppress("ExposedMutableState")
                    class Good
                    """.trimIndent()
                )
            )
            .issues(SuppressionPolicyDetector.ISSUE)
            .run()
            .expectClean()
    }
}
