package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class ModuleDependencyDetectorTest {

    @Test
    fun `feature to feature coupling reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.profile
                    import com.estatia.realestate.apps.feature.home.HomeActivity
                    class Profile { val x = HomeActivity() }
                    """.trimIndent()
                ).to("src/com/estatia/realestate/apps/feature/profile/Profile.kt")
            )
            .issues(ModuleDependencyDetector.FEATURE_COUPLING_ISSUE)
            .run()
            .expectContains("Illegal Feature Coupling")
    }
}
