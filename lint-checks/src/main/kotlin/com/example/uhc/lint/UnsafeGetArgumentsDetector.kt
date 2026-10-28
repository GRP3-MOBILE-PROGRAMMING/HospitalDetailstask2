package com.example.uhc.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/** Flags `getArguments().x` inside a Fragment; the bundle is null unless `setArguments()` was called. */
class UnsafeGetArgumentsDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String> = listOf("getArguments")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (method.parameterList.parametersCount != 0) return
        if (!context.evaluator.isMemberInSubClassOf(method, FRAGMENT_CLASS, false)) return
        if (!node.isDereferenced()) return

        val fix = fix()
            .name("Replace with requireArguments()")
            .replace()
            .text("getArguments()")
            .with("requireArguments()")
            .build()
        context.report(
            ISSUE, node, context.getLocation(node),
            "`getArguments()` can be null; use `requireArguments()` or check for null first",
            fix
        )
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "UhcUnsafeGetArguments",
            briefDescription = "Fragment arguments used without a null check",
            explanation = """
                `Fragment.getArguments()` returns null when the fragment was created with \
                `new MyFragment()` instead of a `newInstance(...)` factory that calls `setArguments()`. \
                The system re-creates fragments after rotation and process death using the no-arg \
                constructor plus the saved arguments bundle, so arguments are the only safe place for \
                fragment input.

                Create fragments through a `newInstance(...)` factory and read the bundle with \
                `requireArguments()` (which fails fast with a clear message) or null-check `getArguments()`.
            """,
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(UnsafeGetArgumentsDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
