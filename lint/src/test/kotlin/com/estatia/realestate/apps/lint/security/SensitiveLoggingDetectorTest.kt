package com.estatia.realestate.apps.lint.security

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class SensitiveLoggingDetectorTest {

    @Test
    fun `logging password reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.ANDROID_LOG,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import android.util.Log
                    
                    class Test {
                        fun login(password: String) {
                            Log.d("Auth", "User password: " + password)
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(SensitiveLoggingDetector.ISSUE)
            .run()
            .expectContains("Potential exposure of sensitive data in logs")
    }

    @Test
    fun `logging generic message is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.ANDROID_LOG,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import android.util.Log
                    
                    class Test {
                        fun run() {
                            Log.d("System", "App started")
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(SensitiveLoggingDetector.ISSUE)
            .run()
            .expectClean()
    }
}
