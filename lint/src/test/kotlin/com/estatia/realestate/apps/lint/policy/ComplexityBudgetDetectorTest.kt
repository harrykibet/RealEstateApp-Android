package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.LintDetectorTest.xml
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class ComplexityBudgetDetectorTest {

    @Test
    fun `long method reports length fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun veryLongMethod() {
                            ${(1..310).joinToString("\n") { "println($it)" }}
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(Law028_SpaghettiMethodDetector.ISSUE)
            .run()
            .expectContains("violates Length Budget")
    }

    @Test
    fun `complex method with many branches reports complexity warning`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun complexMethod(a: Int, b: Int, c: Int) {
                            if (a > 0) {
                                if (b > 0) {
                                    if (c > 0) { println(1) } else { println(2) }
                                } else {
                                    for (i in 0..10) { println(i) }
                                }
                            } else {
                                when (a) {
                                    1 -> println(1)
                                    2 -> println(2)
                                    3 -> println(3)
                                    4 -> println(4)
                                    5 -> println(5)
                                    else -> println(0)
                                }
                            }
                            if (a == 0 && b == 0 || c == 0) { println(3) }
                            try { println(4) } catch (e: Exception) { println(5) }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(Law028_SpaghettiMethodDetector.ISSUE)
            .run()
            .expectContains("violates Complexity Budget")
    }

    @Test
    fun `custom complexity thresholds are respected`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                xml("lint.xml", """
                    <lint>
                        <issue id="SpaghettiMethodFatal">
                            <option name="maxComplexityWarning" value="2" />
                        </issue>
                    </lint>
                """.trimIndent()),
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun simple(a: Boolean, b: Boolean) {
                            if (a) { println(1) } 
                            if (b) { println(2) }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(Law028_SpaghettiMethodDetector.ISSUE)
            .run()
            .expectContains("Complexity Budget (3)")
            .expectContains("WARNING limit is 2")
    }
}
