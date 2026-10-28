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

/** Flags `getActivity().x` / `getContext().x` inside a Fragment, which crashes once the fragment is detached. */
class UnsafeGetActivityDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String> = listOf("getActivity", "getContext")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (method.parameterList.parametersCount != 0) return
        if (!context.evaluator.isMemberInSubClassOf(method, FRAGMENT_CLASS, false)) return
        if (!node.isDereferenced()) return

        val replacement = if (method.name == "getActivity") "requireActivity()" else "requireContext()"
        val fix = fix()
            .name("Replace with $replacement")
            .replace()
            .text("${method.name}()")
            .with(replacement)
            .build()
        context.report(
            ISSUE, node, context.getLocation(node),
            "`${method.name}()` returns null when the fragment is detached; use `$replacement` or check for null first",
            fix
        )
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "UhcUnsafeGetActivity",
            briefDescription = "Unchecked getActivity()/getContext() in a Fragment",
            explanation = """
                `Fragment.getActivity()` and `Fragment.getContext()` return null before `onAttach()` \
                and after `onDetach()`, for example when a network callback or a delayed `Runnable` \
                runs after the user has left the screen. Dereferencing the result directly causes a \
                `NullPointerException`.

                Inside lifecycle methods where the fragment is guaranteed to be attached, call \
                `requireActivity()` / `requireContext()`. In callbacks that may run later, check \
                `isAdded()` or null-check the result before using it.
            """,
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(UnsafeGetActivityDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
