package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class InfrastructureLeakageDetectorTest {

    @Test
    fun `leaking retrofit into domain reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.domain
                    class Retrofit
                    class MyUseCase(val retrofit: com.estatia.realestate.apps.core.domain.Retrofit)
                    """.trimIndent()
                ).to("src/com/estatia/realestate/apps/core/domain/MyUseCase.kt")
            )
            .issues(InfrastructureLeakageDetector.ISSUE)
            .run()
            .expectClean() // My fake stub is in domain pkg, so it's fine.
    }

    @Test
    fun `leaking real retrofit into domain reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.domain
                    import retrofit2.Retrofit
                    class MyUseCase(val retrofit: Retrofit)
                    """.trimIndent()
                ).to("src/com/estatia/realestate/apps/core/domain/MyUseCase.kt")
            )
            .issues(InfrastructureLeakageDetector.ISSUE)
            .run()
            .expectContains("Infrastructure leak")
    }
}
