package com.estatia.realestate.apps.core.testing_architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class Law030_ConstructorBudgetTest {

    @Test
    fun `constructor dependency budget compliance`() {
        Konsist.scopeFromProject()
            .classes()
            .filterNot { ArchitecturalPolicy.TechnicalDebt.ComplexityBudget.contains(it.name) }
            .assertTrue { clazz ->
                clazz.constructors.all { it.parameters.size < 9 }
            }
    }
}
