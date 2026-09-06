package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class Law019_SecretConcurrencyTest {

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
            .issues(Law019_SecretConcurrencyDetector.ISSUE)
            .run()
            .expectContains("Suspend function 'saveData' secretly launches independent work")
    }
}
