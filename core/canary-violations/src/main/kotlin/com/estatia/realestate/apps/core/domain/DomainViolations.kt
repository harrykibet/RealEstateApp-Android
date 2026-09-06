package com.estatia.realestate.apps.core.domain

import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.feature.auth.viewModels.LoginViewModel

/**
 * LAW-003: Infrastructure Leakage
 * Deliberate leakage of database implementation into domain package.
 */
class DomainLeakageCarrier {
    val leakedDataSource: IPropertyLocalDataSource? = null
}

/**
 * LAW-004: Feature Coupling
 * Deliberate coupling to another feature in a domain layer.
 */
class DomainCouplingCarrier {
    val leakedViewModel: LoginViewModel? = null
}
