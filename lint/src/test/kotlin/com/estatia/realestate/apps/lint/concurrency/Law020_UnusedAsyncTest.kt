package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class Law020_UnusedAsyncTest {

    @Test
    fun `unused async result reports error`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.home
                    import kotlinx.coroutines.*
                    
                    class ViewModel {
                        suspend fun load(scope: CoroutineScope) {
                            scope.async { "Data" }
                            println("Done")
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(Law020_UnusedAsyncDetector.ISSUE)
            .run()
            .expectContains("Result of 'async' is ignored")
    }
}
