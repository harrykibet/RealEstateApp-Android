package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class Law024_ContextLeakTest {

    @Test
    fun `context stored in singleton reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.ANDROID_CONTENT,
                Stubs.DAGGER_HILT,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import android.content.Context
                    import javax.inject.Singleton
                    
                    @Singleton
                    object Analytics {
                        var context: Context? = null
                    }
                    """.trimIndent()
                )
            )
            .issues(Law024_ContextLeakDetector.ISSUE)
            .run()
            .expectContains("Context stored in a Singleton object")
    }
}
