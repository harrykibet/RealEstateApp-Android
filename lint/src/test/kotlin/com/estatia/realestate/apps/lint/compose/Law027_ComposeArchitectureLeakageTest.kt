package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class Law027_ComposeArchitectureLeakageTest {

    @Test
    fun `direct repository call in composable reports error`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COMPOSE,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    class MyRepository { fun load() {} }
                    @Composable
                    fun MyUI(repo: MyRepository) {
                        repo.load()
                    }
                    """.trimIndent()
                )
            )
            .issues(Law027_ComposeArchitectureLeakageDetector.ISSUE)
            .run()
            .expectContains("Direct call to architectural component")
    }
}
