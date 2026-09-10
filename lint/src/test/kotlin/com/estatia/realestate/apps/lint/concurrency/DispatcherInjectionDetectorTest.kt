package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class DispatcherInjectionDetectorTest {

    @Test
    fun `hardcoded Dispatchers IO reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.DAGGER,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.Dispatchers
                    import kotlinx.coroutines.withContext
                    
                    class MyRepo {
                        suspend fun doWork() = withContext(Dispatchers.IO) { }
                    }
                    """.trimIndent()
                )
            )
            .issues(DispatcherInjectionDetector.ISSUE)
            .run()
            .expectContains("Hardcoded Dispatcher 'IO' is forbidden")
    }

    @Test
    fun `injected dispatcher is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.DAGGER,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.CoroutineDispatcher
                    
                    class MyRepo(private val ioDispatcher: CoroutineDispatcher)
                    """.trimIndent()
                )
            )
            .issues(DispatcherInjectionDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `hardcoded Dispatchers in DI module is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.DAGGER,
                kotlin(
                    """
                    package com.estatia.realestate.apps.di
                    import kotlinx.coroutines.Dispatchers
                    import dagger.Module
                    import dagger.Provides
                    
                    @Module
                    class NetworkModule {
                        @Provides
                        fun provideDispatcher() = Dispatchers.IO
                    }
                    """.trimIndent()
                )
            )
            .issues(DispatcherInjectionDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `file named Module but not annotated reports violation`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.DAGGER,
                kotlin(
                    "src/com/estatia/realestate/apps/PaymentsModuleUtils.kt",
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.Dispatchers
                    
                    class PaymentsModuleUtils {
                        fun getIO() = Dispatchers.IO
                    }
                    """.trimIndent()
                )
            )
            .issues(DispatcherInjectionDetector.ISSUE)
            .run()
            .expectContains("Hardcoded Dispatcher 'IO' is forbidden")
    }
}
