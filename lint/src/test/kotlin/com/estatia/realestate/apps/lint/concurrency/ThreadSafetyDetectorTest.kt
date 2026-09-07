package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class ThreadSafetyDetectorTest {

    @Test
    fun `unsafe collection in singleton reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
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
                    class MyRepo {
                        private val cache = HashMap<String, String>()
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectContains("Unsafe collection 'java.util.HashMap' used in a multi-threaded component")
    }

    @Test
    fun `unsafe collection in ViewModel reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.VIEWMODEL,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.lifecycle.ViewModel
                    import java.util.HashMap
                    
                    class MyViewModel : ViewModel() {
                        private val localState = HashMap<String, Int>()
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectContains("Unsafe collection 'java.util.HashMap' used in a multi-threaded component")
    }

    @Test
    fun `unsafe collection in Repository suffix class reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import java.util.ArrayList
                    
                    class PropertyRepository {
                        private val observers = ArrayList<String>()
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectContains("Unsafe collection 'java.util.ArrayList' used in a multi-threaded component")
    }

    @Test
    fun `concurrent collection is clean`() {
        lint()
            .testModes(TestMode.DEFAULT)
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
                    class MyRepo {
                        private val cache = ConcurrentHashMap<String, String>()
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectClean()
    }
}
