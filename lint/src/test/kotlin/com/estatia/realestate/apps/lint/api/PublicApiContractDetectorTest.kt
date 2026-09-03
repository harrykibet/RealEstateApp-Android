package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.LintDetectorTest.java
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class PublicApiContractDetectorTest {

    private val mutableStateFlowStub = kotlin(
        """
        package kotlinx.coroutines.flow
        class MutableStateFlow<T>(value: T) : StateFlow<T>
        interface StateFlow<out T>
        """.trimIndent()
    )

    private val firebaseUserStub = kotlin(
        """
        package com.google.firebase.auth
        class FirebaseUser
        """.trimIndent()
    )

    private val coroutineScopeStub = kotlin(
        """
        package kotlinx.coroutines
        interface CoroutineScope
        """.trimIndent()
    )

    @Test
    fun `public MutableStateFlow reports error`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                mutableStateFlowStub,
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.home
                    import kotlinx.coroutines.flow.MutableStateFlow
                    
                    class HomeViewModel {
                        val uiState = MutableStateFlow("Initial")
                    }
                    """.trimIndent()
                )
            )
            .issues(PublicApiContractDetector.MUTABLE_STATE_ISSUE)
            .run()
            .expectContains("Return type of 'getUiState' exposes a mutable container or framework primitive 'kotlinx.coroutines.flow.MutableStateFlow'")
    }

    @Test
    fun `private MutableStateFlow with underscore is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                mutableStateFlowStub,
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.home
                    import kotlinx.coroutines.flow.MutableStateFlow
                    import kotlinx.coroutines.flow.StateFlow
                    
                    class HomeViewModel {
                        private val _uiState = MutableStateFlow("Initial")
                        val uiState: StateFlow<String> = _uiState
                    }
                    """.trimIndent()
                )
            )
            .issues(PublicApiContractDetector.MUTABLE_STATE_ISSUE, PublicApiContractDetector.BACKING_PROPERTY_CONVENTION_ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `Firebase type in public method return reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                firebaseUserStub,
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.data
                    import com.google.firebase.auth.FirebaseUser
                    
                    class AuthRepository {
                        fun getCurrentUser(): FirebaseUser? = null
                    }
                    """.trimIndent()
                )
            )
            .issues(PublicApiContractDetector.IMPLEMENTATION_LEAK_ISSUE)
            .run()
            .expectContains("Return type of 'getCurrentUser' exposes an implementation-specific type 'com.google.firebase.auth.FirebaseUser'")
    }

    @Test
    fun `CoroutineScope in public property reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                coroutineScopeStub,
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.search
                    import kotlinx.coroutines.CoroutineScope
                    
                    class SearchManager(
                        val externalScope: CoroutineScope
                    )
                    """.trimIndent()
                )
            )
            .issues(PublicApiContractDetector.MUTABLE_STATE_ISSUE)
            .run()
            .expectContains("Return type of 'getExternalScope' exposes a mutable container")
    }

    @Test
    fun `Java variant of implementation leakage reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                firebaseUserStub,
                java(
                    """
                    package com.estatia.realestate.apps.core.data;
                    import com.google.firebase.auth.FirebaseUser;
                    
                    public class LegacyAuth {
                        public FirebaseUser getUser() { return null; }
                    }
                    """.trimIndent()
                )
            )
            .issues(PublicApiContractDetector.IMPLEMENTATION_LEAK_ISSUE)
            .run()
            .expectContains("Return type of 'getUser' exposes an implementation-specific type 'com.google.firebase.auth.FirebaseUser'")
    }
}
