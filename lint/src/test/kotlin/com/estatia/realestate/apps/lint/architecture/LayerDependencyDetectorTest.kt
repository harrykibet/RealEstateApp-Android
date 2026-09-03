package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class LayerDependencyDetectorTest {

    @Test
    fun `domain layer importing android framework reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.domain
                    import android.os.Bundle
                    
                    class MyUseCase
                    """.trimIndent()
                )
            )
            .issues(LayerDependencyDetector.ISSUE)
            .run()
            .expectContains("Domain layer purity violation: Cannot import Android Framework")
    }

    @Test
    fun `model layer importing androidX reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.model
                    import androidx.lifecycle.LiveData
                    
                    data class User(val id: String)
                    """.trimIndent()
                )
            )
            .issues(LayerDependencyDetector.ISSUE)
            .run()
            .expectContains("Model layer violation: Models must be pure Kotlin")
    }

    @Test
    fun `presentation layer importing firebase reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.home.ui
                    import com.google.firebase.auth.FirebaseAuth
                    
                    class HomeFragment
                    """.trimIndent()
                )
            )
            .issues(LayerDependencyDetector.ISSUE)
            .run()
            .expectContains("Presentation layer violation: Cannot use infrastructure details (Firebase) directly in UI")
    }
}
