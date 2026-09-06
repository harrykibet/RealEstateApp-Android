package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class Law023_LifecycleLeakTest {

    @Test
    fun `activity stored in ViewModel reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.ANDROID_APP,
                Stubs.VIEWMODEL,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import android.app.Activity
                    import androidx.lifecycle.ViewModel
                    
                    class MyViewModel(private val activity: Activity) : ViewModel()
                    """.trimIndent()
                )
            )
            .issues(Law023_LifecycleLeakDetector.ISSUE)
            .run()
            .expectContains("is a lifecycle-bound type. Storing it here will cause memory leaks")
    }
}
