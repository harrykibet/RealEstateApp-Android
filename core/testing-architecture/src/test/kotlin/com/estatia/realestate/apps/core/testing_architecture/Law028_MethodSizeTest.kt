package com.estatia.realestate.apps.core.testing_architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class Law028_MethodSizeTest {

    @Test
    fun `method size compliance`() {
        Konsist.scopeFromProject()
            .classes()
            .filterNot { ArchitecturalPolicy.TechnicalDebt.ComplexityBudget.contains(it.name) }
            .assertTrue { clazz ->
                clazz.functions().all { it.text.lines().size < 300 }
            }
    }
}
