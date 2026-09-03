package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class ResponsibilityBoundaryDetectorTest {

    @Test
    fun `ViewModel referencing Firebase reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import com.google.firebase.firestore.FirebaseFirestore
                    class HomeViewModel {
                        fun test() { FirebaseFirestore.getInstance() }
                    }
                    """.trimIndent()
                )
            )
            .issues(ResponsibilityBoundaryDetector.ISSUE)
            .run()
            .expectContains("Illegal Layer Mixing")
    }
}
