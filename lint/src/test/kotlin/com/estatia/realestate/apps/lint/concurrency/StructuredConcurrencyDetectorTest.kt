package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class StructuredConcurrencyDetectorTest {

    @Test
    fun `suspend function launching on external scope reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.data
                    import kotlinx.coroutines.CoroutineScope
                    import kotlinx.coroutines.launch
                    
                    class Repository(private val scope: CoroutineScope) {
                        suspend fun saveData() {
                            scope.launch { 
                                // Perform work
                            }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(StructuredConcurrencyDetector.SECRET_CONCURRENCY_ISSUE)
            .run()
            .expectContains("Suspend function 'saveData' secretly launches independent work")
    }

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
            .issues(StructuredConcurrencyDetector.UNUSED_ASYNC_ISSUE)
            .run()
            .expectContains("Result of 'async' is ignored")
    }
}
