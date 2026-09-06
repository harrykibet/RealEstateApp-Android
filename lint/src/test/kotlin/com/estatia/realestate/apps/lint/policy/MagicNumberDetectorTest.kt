package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.LintDetectorTest.xml
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import com.android.tools.lint.detector.api.Severity
import org.junit.Test

class MagicNumberDetectorTest {

    @Test
    fun `magic number in logic reports warning`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun check(age: Int) {
                            if (age > 21) {
                                println("Old enough")
                            }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(MagicNumberDetector.ISSUE)
            .run()
            .expectContains("Magic number '21' detected")
    }

    @Test
    fun `allowed number in logic is clean`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun check(value: Int) {
                            if (value == 100) {
                                println("Century")
                            }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(MagicNumberDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `custom allowed numbers in lint xml are respected`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                xml("lint.xml", """
                    <lint>
                        <issue id="MagicNumber">
                            <option name="allowedNumbers" value="21,42" />
                        </issue>
                    </lint>
                """.trimIndent()),
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun check(value: Int) {
                            if (value == 21 || value == 42) {
                                println("Special")
                            }
                            if (value == 100) { // Should now be magic
                                println("Not special")
                            }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(MagicNumberDetector.ISSUE)
            .run()
            .expectContains("Magic number '100' detected")
            .expectCount(1, Severity.WARNING)
    }

    @Test
    fun `assignment to constant is clean`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        companion object {
                            const val MIN_AGE = 21
                        }
                        val limit = 42
                    }
                    """.trimIndent()
                )
            )
            .issues(MagicNumberDetector.ISSUE)
            .run()
            .expectClean()
    }
}
