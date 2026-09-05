package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class MainThreadWorkDetectorTest {

    @Test
    fun `Thread sleep in ViewModel reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.VIEWMODEL,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.lifecycle.ViewModel
                    class HomeViewModel : ViewModel() {
                        fun wait() {
                            Thread.sleep(1000)
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(MainThreadWorkDetector.ISSUE)
            .run()
            .expectContains("Blocking call 'sleep' detected in a UI-sensitive context")
    }

    @Test
    fun `blocking read in suspend function reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import java.io.InputStream
                    class MyRepo {
                        suspend fun load(stream: InputStream) {
                            stream.read()
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(MainThreadWorkDetector.ISSUE)
            .run()
            .expectContains("Blocking call 'read' detected inside suspend function")
    }
}
