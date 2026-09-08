package com.estatia.realestate.apps.lint.registry

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor

/**
 * The central authority for Estatia engineering rules.
 * Uses EstatiaPolicyGroups as the Single Source of Truth for its issue list.
 */
class EstatiaIssueRegistry : IssueRegistry() {
    
    override val issues = EstatiaPolicyGroups.all

    /**
     * Pinning the API version ensures cross-environment compatibility.
     * Value 16 corresponds to Lint API 31.4.0 (AGP 8.4+).
     */
    override val api: Int = 16

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "Estatia Engineering",
        feedbackUrl = "https://github.com/estatia/realestate/issues",
        contact = "https://github.com/estatia/realestate"
    )
}
