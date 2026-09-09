package com.estatia.realestate.apps.core.canary_violations.specs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

/**
 * Hub to ensure all specs are visible to the compiler and analyzer.
 */
@Composable
public fun CanarySpecHub(
    repo: Law009_Positive_Repository,
    viewModel: Law012_018_023_029_Positive_ViewModel,
    monster: Law030_Positive_Spec
) {
    // API Design
    repo.getRawData()
    
    // Compose
    Law001_002_026_027_Positive_Compose().PositiveCanary(repo, mutableStateOf(0))
    
    // State
    viewModel.mutate()
    
    // Complexity
    monster.r1.mutate()
    
    // Environment
    Environment_Positive_Spec().now()
}
