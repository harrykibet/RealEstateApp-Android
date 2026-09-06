package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class ModuleDependencyDetectorTest {

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
            .issues(ModuleDependencyDetector.FEATURE_COUPLING_ISSUE)
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
            .issues(ModuleDependencyDetector.FEATURE_COUPLING_ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `infrastructure leaked into domain reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .allowCompilationErrors()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.domain
                    import com.estatia.realestate.apps.core.database.UserDao
                    
                    class MyUseCase
                    """.trimIndent()
                )
            )
            .issues(ModuleDependencyDetector.IMPLEMENTATION_LEAKAGE_ISSUE)
            .run()
            .expectContains("Infrastructure implementation 'com.estatia.realestate.apps.core.database.UserDao' leaked into pure layer")
    }

    @Test
    fun `firebase leaked into model reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .allowCompilationErrors()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.model
                    import com.google.firebase.auth.FirebaseUser
                    
                    class UserModel
                    """.trimIndent()
                )
            )
            .issues(ModuleDependencyDetector.IMPLEMENTATION_LEAKAGE_ISSUE)
            .run()
            .expectContains("Infrastructure implementation 'com.google.firebase.auth.FirebaseUser' leaked into pure layer")
    }
}
