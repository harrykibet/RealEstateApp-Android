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

    @Test
    fun `manual CoroutineScope in App Initializer is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.startup.Initializer
                    import kotlinx.coroutines.CoroutineScope
                    import kotlinx.coroutines.Dispatchers
                    
                    class MyInitializer : Initializer<Unit> {
                        override fun create(context: android.content.Context) {
                            val scope = CoroutineScope(Dispatchers.Main)
                        }
                    }
                    """.trimIndent()
                ),
                kotlin(
                    """
                    package androidx.startup
                    interface Initializer<T> {
                        fun create(context: android.content.Context)
                    }
                    """.trimIndent()
                )
            )
            .issues(ForbiddenScopeDetector.FORBIDDEN_SCOPE_ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `file named Initializer but not implementing interface reports violation`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                kotlin(
                    "src/com/estatia/realestate/apps/FakeInitializer.kt",
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.CoroutineScope
                    import kotlinx.coroutines.Dispatchers
                    
                    class FakeInitializer {
                        fun init() {
                            val scope = CoroutineScope(Dispatchers.IO)
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(ForbiddenScopeDetector.FORBIDDEN_SCOPE_ISSUE)
            .run()
            .expectContains("Manual instantiation of 'CoroutineScope' is forbidden")
    }
}
