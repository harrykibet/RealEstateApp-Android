package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class ForbiddenScopeDetectorTest {

    @Test
    fun `GlobalScope usage reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.DAGGER,
                Stubs.VIEWMODEL,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.GlobalScope
                    import kotlinx.coroutines.launch
                    
                    class Test {
                        fun run() {
                            GlobalScope.launch { }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(ForbiddenScopeDetector.FORBIDDEN_SCOPE_ISSUE)
            .run()
            .expectContains("Usage of 'GlobalScope' is forbidden")
    }

    @Test
    fun `manual CoroutineScope in business logic reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.DAGGER,
                Stubs.VIEWMODEL,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.CoroutineScope
                    import kotlinx.coroutines.Dispatchers
                    
                    class MyRepo {
                        val scope = CoroutineScope(Dispatchers.IO)
                    }
                    """.trimIndent()
                )
            )
            .issues(ForbiddenScopeDetector.FORBIDDEN_SCOPE_ISSUE)
            .run()
            .expectContains("Manual instantiation of 'CoroutineScope' is forbidden in business logic")
    }
}
