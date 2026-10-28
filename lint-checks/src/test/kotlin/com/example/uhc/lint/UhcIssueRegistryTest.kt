package com.example.uhc.lint

import com.android.tools.lint.client.api.LintClient
import org.junit.Assert.assertEquals
import org.junit.Test

class UhcIssueRegistryTest {
    @Test
    fun registersAllUhcIssues() {
        LintClient.clientName = LintClient.CLIENT_UNIT_TESTS
        val ids = UhcIssueRegistry().issues.map { it.id }.toSet()
        assertEquals(
            setOf(
                "UhcCommitAfterStateSaved",
                "UhcUnsafeGetActivity",
                "UhcViewAccessAfterDestroyView",
                "UhcUnsafeGetArguments",
            ),
            ids
        )
    }
}
