package com.estatia.realestate.apps.core.testing_architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class Law029_ClassSizeTest {

    @Test
    fun `class size compliance`() {
        Konsist.scopeFromProject()
            .classes()
            .filterNot { ArchitecturalPolicy.TechnicalDebt.ComplexityBudget.contains(it.name) }
            .assertTrue { clazz ->
                clazz.text.lines().size < 1000
            }
    }
}
