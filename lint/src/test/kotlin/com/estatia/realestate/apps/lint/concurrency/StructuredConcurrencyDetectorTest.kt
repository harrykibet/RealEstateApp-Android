package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class StructuredConcurrencyDetectorTest {

    private val coroutineStubs = kotlin(
        """
        package kotlinx.coroutines
        interface CoroutineScope
        fun CoroutineScope.launch(block: suspend () -> Unit) {}
        fun <T> CoroutineScope.async(block: suspend () -> T): Deferred<T> = TODO()
        interface Deferred<out T> { suspend fun await(): T }
        fun CoroutineExceptionHandler(handler: (Any, Throwable) -> Unit): CoroutineExceptionHandler = TODO()
        interface CoroutineExceptionHandler
        object Dispatchers { val IO: Any = TODO() }
        suspend fun <T> withContext(context: Any, block: suspend () -> T): T = TODO()
        """.trimIndent()
    )

    @Test
    fun `suspend function launching on external scope reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                coroutineStubs,
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
                coroutineStubs,
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
