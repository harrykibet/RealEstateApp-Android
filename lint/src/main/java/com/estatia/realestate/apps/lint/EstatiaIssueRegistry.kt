package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

class EstatiaIssueRegistry : IssueRegistry() {
    override val issues = listOf(
        DesignSystemDetector.ISSUE,
        ModulePackageDetector.ISSUE
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12 // works with Studio 4.2 or newer

    override val vendor: Vendor = Vendor(
        vendorName = "Estatia",
        feedbackUrl = "https://github.com/estatia/realestate/issues",
        contact = "https://github.com/estatia/realestate"
    )
}
