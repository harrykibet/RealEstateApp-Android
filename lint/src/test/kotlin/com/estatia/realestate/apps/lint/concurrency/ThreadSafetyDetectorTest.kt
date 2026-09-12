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
                        private var cache = HashMap<String, String>()
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectContains("Unsafe collection 'java.util.HashMap' mutated in a multi-threaded component")
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
                        private var localState = HashMap<String, Int>()
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectContains("Unsafe collection 'java.util.HashMap' mutated in a multi-threaded component")
    }

    @Test
    fun `unsafe collection in Repository suffix class reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.ESTATIA_ARCH,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import com.estatia.realestate.apps.core.architecture.annotations.Identity
                    import java.util.ArrayList
                    
                    @Identity.Repository
                    class PropertyRepository {
                        private var observers = ArrayList<String>()
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectContains("Unsafe collection 'java.util.ArrayList' mutated in a multi-threaded component")
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

    @Test
    fun `read-only val hashmap is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.DAGGER_HILT,
                Stubs.VIEWMODEL,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import javax.inject.Singleton
                    import java.util.HashMap
                    
                    @Singleton
                    class MyComponent {
                        private val lookup = HashMap<String, Int>()
                        
                        init {
                            lookup.put("a", 1) // Mutation in init is allowed
                        }
                        
                        fun get(key: String): Int? = lookup.get(key) // Read is fine
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `mutated val hashmap reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.DAGGER_HILT,
                Stubs.VIEWMODEL,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import javax.inject.Singleton
                    import java.util.HashMap
                    
                    @Singleton
                    class MyComponent {
                        private val cache = HashMap<String, Int>()
                        
                        fun update(key: String, value: Int) {
                            cache.put(key, value) // Mutation in method is forbidden
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectContains("Unsafe collection 'java.util.HashMap' mutated")
    }

    @Test
    fun `var hashmap always reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.DAGGER_HILT,
                Stubs.VIEWMODEL,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import javax.inject.Singleton
                    import java.util.HashMap
                    
                    @Singleton
                    class MyComponent {
                        private var dynamicLookup = HashMap<String, Int>() // var is always forbidden
                    }
                    """.trimIndent()
                )
            )
            .issues(ThreadSafetyDetector.ISSUE)
            .run()
            .expectContains("Unsafe collection 'java.util.HashMap' mutated")
    }
}
