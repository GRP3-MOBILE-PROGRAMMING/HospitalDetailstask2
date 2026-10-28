package com.example.uhc.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

/** Registers the UHC Fragment lifecycle checks. Listed in META-INF/services so lint can find it. */
class UhcIssueRegistry : IssueRegistry() {
    override val issues: List<Issue> = listOf(
        CommitAfterStateSavedDetector.ISSUE,
        UnsafeGetActivityDetector.ISSUE,
        ViewAccessAfterDestroyViewDetector.ISSUE,
        UnsafeGetArgumentsDetector.ISSUE,
    )

    override val api: Int = CURRENT_API

    override val vendor: Vendor = Vendor(
        vendorName = "UHC DevOps",
        identifier = "uhc-lint-checks",
    )
}
