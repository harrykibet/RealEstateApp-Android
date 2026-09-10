package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import com.estatia.realestate.apps.lint.api.Law009_ResultWrapperDetector
import com.estatia.realestate.apps.lint.concurrency.ForbiddenScopeDetector
import org.junit.Test

class SuppressionPolicyDetectorTest {

    @Test
    fun `suppression of FATAL rule reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.RESULT,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    @Suppress("ForbiddenCoroutineScope")
                    class Bad
                    """.trimIndent()
                )
            )
            .issues(SuppressionPolicyDetector.ISSUE, ForbiddenScopeDetector.FORBIDDEN_SCOPE_ISSUE)
            .run()
            .expectContains("cannot be suppressed")
    }

    @Test
    fun `suppression of ERROR rule without justification reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.RESULT,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    @Suppress("MissingResultWrapper")
                    class Bad
                    """.trimIndent()
                )
            )
            .issues(SuppressionPolicyDetector.ISSUE, Law009_ResultWrapperDetector.ISSUE)
            .run()
            .expectContains("requires an immediately preceding justification comment")
    }

    @Test
    fun `suppression of ERROR rule with correct justification is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.RESULT,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    // Justification: MissingResultWrapper - necessary for legacy interop
                    @Suppress("MissingResultWrapper")
                    class Good
                    """.trimIndent()
                )
            )
            .issues(SuppressionPolicyDetector.ISSUE, Law009_ResultWrapperDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `multiple suppressions with multiple justifications in a block is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.RESULT,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    // Justification: ExposedMutableState - legacy interop
                    // Justification: MissingResultWrapper - legacy interop
                    @Suppress("ExposedMutableState", "MissingResultWrapper")
                    class Good
                    """.trimIndent()
                )
            )
            .issues(SuppressionPolicyDetector.ISSUE, ExposedMutableStateDetector.ISSUE, Law009_ResultWrapperDetector.ISSUE)
            .run()
            .expectClean()
    }
}
