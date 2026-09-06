package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class Law021_MisplacedExceptionHandlerTest {

    @Test
    fun `ExceptionHandler in withContext reports warning`() {
        lint()
            .skipTestModes(TestMode.REORDER_ARGUMENTS)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.*
                    
                    class Test {
                        suspend fun doWork(ceh: CoroutineExceptionHandler) {
                            withContext(Dispatchers.IO + ceh) {
                                // Work
                            }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(Law021_MisplacedExceptionHandlerDetector.ISSUE)
            .run()
            .expectContains("CoroutineExceptionHandler used in 'withContext' will be ignored")
    }

    @Test
    fun `ExceptionHandler in launch is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.*
                    
                    class Test(val scope: CoroutineScope) {
                        fun doWork(ceh: CoroutineExceptionHandler) {
                            scope.launch(Dispatchers.IO + ceh) {
                                // Work
                            }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(Law021_MisplacedExceptionHandlerDetector.ISSUE)
            .run()
            .expectClean()
    }
}
