package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_027

import androidx.compose.runtime.Composable
import com.estatia.realestate.apps.core.architecture.annotations.Identity
import com.estatia.realestate.apps.core.architecture.annotations.Ui

/**
 * LAW-027: Tier 1 - Syntactic Bypasses
 */

@Identity.Repository
public class DirectRepository {
    public fun load() {}
}

@Composable
@Ui.UiScreen
public fun SyntacticDataLeakage(repo: DirectRepository) {
    // [CANARY:POSITIVE:ComposeArchitectureLeakage:BLOCK:Direct call]
    repo.load()
}
