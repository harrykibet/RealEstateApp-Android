package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class UnboundedBufferDetectorTest {

    @Test
    fun `unbounded flow buffer reports warning`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.flow.flowOf
                    import kotlinx.coroutines.flow.buffer
                    
                    fun test() {
                        flowOf(1).buffer()
                    }
                    """.trimIndent()
                )
            )
            .issues(UnboundedBufferDetector.ISSUE)
            .run()
            .expectContains("Unbounded buffer detected")
    }

    @Test
    fun `bounded flow buffer is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.flow.flowOf
                    import kotlinx.coroutines.flow.buffer
                    
                    fun test() {
                        flowOf(1).buffer(16)
                    }
                    """.trimIndent()
                )
            )
            .issues(UnboundedBufferDetector.ISSUE)
            .run()
            .expectClean()
    }
}
