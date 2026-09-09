package com.estatia.realestate.apps.core.domain

import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource // [CANARY:POSITIVE:InfrastructureLeakage]

/**
 * Trigger for InfrastructureLeakage (LAW-003)
 */
class DomainViolations {
    val leakedDataSource: IPropertyLocalDataSource? = null
}
