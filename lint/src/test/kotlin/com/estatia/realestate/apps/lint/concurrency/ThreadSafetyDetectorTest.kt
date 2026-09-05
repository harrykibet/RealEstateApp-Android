package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class ThreadSafetyDetectorTest {

    @Test
    fun `unsafe HashMap in Singleton reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.DAGGER_HILT,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import javax.inject.Singleton
                    import java.util.HashMap
                    
                    @Singleton
                    class Cache {
                        private val map = HashMap<String, String>()
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectContains("Unsafe collection 'java.util.HashMap' used in a Singleton")
    }

    @Test
    fun `safe collection in Singleton is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.DAGGER_HILT,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import javax.inject.Singleton
                    import java.util.concurrent.ConcurrentHashMap
                    
                    @Singleton
                    class Cache {
                        private val map = ConcurrentHashMap<String, String>()
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectClean()
    }
}
