package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.LintDetectorTest.xml
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class ComplexityBudgetDetectorTest {

    @Test
    fun `long method reports length smell`() {
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
            .expectContains("Length Risk (312)")
            .expectContains("Recommended limit is 300")
    }

    @Test
    fun `deeply nested method reports nesting depth smell`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun deeplyNested() {
                            if (true) {
                                if (true) {
                                    if (true) {
                                        if (true) {
                                            if (true) {
                                                if (true) {
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(Law028_SpaghettiMethodDetector.ISSUE)
            .run()
            .expectContains("Nesting Depth Risk (6)")
            .expectContains("Recommended limit is 5")
    }

    @Test
    fun `method with too many calls reports fan-out smell`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun highFanOut() {
                            ${(1..25).joinToString("\n") { "call$it()" }}
                        }
                        ${(1..25).joinToString("\n") { "fun call$it() {}" }}
                    }
                    """.trimIndent()
                )
            )
            .issues(Law028_SpaghettiMethodDetector.ISSUE)
            .run()
            .expectContains("Fan-out Risk (25)")
    }

    @Test
    fun `class with too much mutable state reports smell`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class StateMonster {
                        var v1 = 0
                        var v2 = 0
                        var v3 = 0
                        var v4 = 0
                        var v5 = 0
                        var v6 = 0
                        var v7 = 0
                        var v8 = 0
                        var v9 = 0
                    }
                    """.trimIndent()
                )
            )
            .issues(Law029_GodObjectDetector.ISSUE)
            .run()
            .expectContains("Mutable State Risk (9)")
            .expectContains("Recommended limit is 8")
    }

    @Test
    fun `class with large public surface reports smell`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class SurfaceMonster {
                        ${(1..45).joinToString("\n") { "fun api$it() {}" }}
                    }
                    """.trimIndent()
                )
            )
            .issues(Law029_GodObjectDetector.ISSUE)
            .run()
            .expectContains("Public Surface Area Risk (45)")
            .expectContains("Recommended limit is 40")
    }

    @Test
    fun `custom thresholds in lint xml are respected`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                xml("lint.xml", """
                    <lint>
                        <issue id="GodObjectFatal">
                            <option name="maxMutableStateError" value="2" />
                        </issue>
                    </lint>
                """.trimIndent()),
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class SmallState {
                        var a = 1
                        var b = 2
                        var c = 3
                    }
                    """.trimIndent()
                )
            )
            .issues(Law029_GodObjectDetector.ISSUE)
            .run()
            .expectContains("Mutable State Risk (3)")
            .expectContains("Recommended limit is 2")
    }
}
