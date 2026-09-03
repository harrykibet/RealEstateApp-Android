package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class VisibilityModifierDetectorTest {

    @Test
    fun `class without visibility modifier reports error`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class BadClass { }
                    """.trimIndent()
                )
            )
            .issues(VisibilityModifierDetector.ISSUE)
            .run()
            .expectContains("Explicit visibility modifier (public, internal, private) is required for class 'BadClass'")
    }

    @Test
    fun `internal class is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    internal class GoodClass { }
                    """.trimIndent()
                )
            )
            .issues(VisibilityModifierDetector.ISSUE)
            .run()
            .expectClean()
    }
}
