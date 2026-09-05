package com.estatia.realestate.apps.lint.testing

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class MockInProductionDetectorTest {

    @Test
    fun `mockk usage in production reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.MOCKK,
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.data
                    import io.mockk.mockk
                    
                    class MyRepo {
                        val mock = mockk<String>()
                    }
                    """.trimIndent()
                ).to("src/com/estatia/realestate/apps/core/data/MyRepo.kt")
            )
            .issues(MockInProductionDetector.ISSUE)
            .run()
            .expectContains("Testing library usage detected in production code")
    }

    @Test
    fun `mockk usage in tests is clean`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                Stubs.MOCKK,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import io.mockk.mockk
                    class MyTest {
                        val mock = mockk<String>()
                    }
                    """.trimIndent()
                ).to("src/test/kotlin/com/estatia/realestate/apps/MyTest.kt")
            )
            .issues(MockInProductionDetector.ISSUE)
            .run()
            .expectClean()
    }
}
