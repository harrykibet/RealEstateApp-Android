package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.LintDetectorTest.xml
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class ComplexityBudgetDetectorTest {

    @Test
    fun `long method reports spaghetti fatal`() {
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
            .expectContains("SpaghettiMethodFatal")
            .expectContains("is too large")
    }

    @Test
    fun `large class reports god object fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class GodObject {
                        ${(1..1010).joinToString("\n") { "val field$it = $it" }}
                    }
                    """.trimIndent()
                )
            )
            .issues(Law029_GodObjectDetector.ISSUE)
            .run()
            .expectContains("GodObjectFatal")
            .expectContains("limit is 1000")
    }

    @Test
    fun `constructor with many dependencies reports orchestration monster`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Monster(
                        d1: Any, d2: Any, d3: Any, d4: Any, d5: Any, 
                        d6: Any, d7: Any, d8: Any, d9: Any, d10: Any
                    )
                    """.trimIndent()
                )
            )
            .issues(Law030_OrchestrationMonsterDetector.ISSUE)
            .run()
            .expectContains("OrchestrationMonsterError")
            .expectContains("Constructor has 10 dependencies")
    }

    @Test
    fun `custom thresholds in lint xml are respected`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                xml("lint.xml", """
                    <lint>
                        <issue id="OrchestrationMonsterError">
                            <option name="errorThreshold" value="3" />
                        </issue>
                    </lint>
                """.trimIndent()),
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class SmallMonster(d1: Any, d2: Any, d3: Any)
                    """.trimIndent()
                )
            )
            .issues(Law030_OrchestrationMonsterDetector.ISSUE)
            .run()
            .expectContains("OrchestrationMonsterError")
            .expectContains("limit is 3")
    }
}
