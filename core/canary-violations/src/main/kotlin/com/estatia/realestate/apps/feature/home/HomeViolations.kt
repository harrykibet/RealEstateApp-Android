package com.estatia.realestate.apps.feature.home

import com.estatia.realestate.apps.feature.auth.viewModels.LoginViewModel // [CANARY:POSITIVE:FeatureCouplingViolation]

/**
 * Trigger for FeatureCouplingViolation (LAW-004)
 */
class HomeViolations {
    val leaked: LoginViewModel? = null
}
