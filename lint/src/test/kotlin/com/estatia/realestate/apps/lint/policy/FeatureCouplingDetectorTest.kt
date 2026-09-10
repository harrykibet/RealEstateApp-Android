package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class FeatureCouplingDetectorTest {

    @Test
    fun `feature module depending on another feature module reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .allowCompilationErrors()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.home
                    import com.estatia.realestate.apps.feature.auth.AuthViewModel
                    
                    class HomeUI
                    """.trimIndent()
                )
            )
            .issues(FeatureCouplingDetector.ISSUE)
            .run()
            .expectContains("Feature module 'home' cannot depend on feature 'auth'")
    }

    @Test
    fun `feature module depending on shared_ui is clean`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .allowCompilationErrors()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.home
                    import com.estatia.realestate.apps.feature.shared_ui.CommonButton
                    
                    class HomeUI
                    """.trimIndent()
                )
            )
            .issues(FeatureCouplingDetector.ISSUE)
            .run()
            .expectClean()
    }
}
