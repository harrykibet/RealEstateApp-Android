package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class LifecycleLeakDetectorTest {

    @Test
    fun `activity stored in ViewModel reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import android.app.Activity
                    import androidx.lifecycle.ViewModel
                    
                    class MyViewModel(private val activity: Activity) : ViewModel()
                    """.trimIndent()
                )
            )
            .issues(LifecycleLeakDetector.LEAK_ISSUE)
            .run()
            .expectContains("is a lifecycle-bound type. Storing it here will cause memory leaks")
    }

    @Test
    fun `context stored in singleton reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import android.content.Context
                    
                    object Analytics {
                        var context: Context? = null
                    }
                    """.trimIndent()
                )
            )
            .issues(LifecycleLeakDetector.LEAK_ISSUE)
            .run()
            .expectContains("Context stored in a Singleton object")
    }
}
