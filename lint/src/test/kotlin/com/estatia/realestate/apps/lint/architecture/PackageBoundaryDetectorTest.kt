package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class PackageBoundaryDetectorTest {

    @Test
    fun `package name violation reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    "package com.wrong.pkg\nclass Test"
                ).to("src/com/estatia/realestate/apps/core/network/Test.kt")
            )
            .issues(PackageBoundaryDetector.ISSUE)
            .run()
            .expectContains("Package name 'com.wrong.pkg' must start with")
    }
}
