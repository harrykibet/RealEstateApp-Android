package com.estatia.realestate.apps.lint.registry

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

/**
 * The central authority for Estatia engineering rules.
 * 
 * Grouped by policy surface to reflect the organizational model.
 */
class EstatiaIssueRegistry : IssueRegistry() {
    
    override val issues = buildList {
        addAll(ArchitectureIssues.all)
        addAll(ConcurrencyIssues.all)
        addAll(ApiIssues.all)
        addAll(ComposeIssues.all)
        addAll(SecurityIssues.all)
        addAll(PerformanceIssues.all)
        addAll(TestingIssues.all)
        addAll(CodeHealthIssues.all)
    }

    override val api: Int = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "Estatia Engineering",
        feedbackUrl = "https://github.com/estatia/realestate/issues",
        contact = "https://github.com/estatia/realestate"
    )
}
