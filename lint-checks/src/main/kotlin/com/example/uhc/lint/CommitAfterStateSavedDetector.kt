package com.example.uhc.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiAnonymousClass
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * Flags `FragmentTransaction.commit()` where it can run after `onSaveInstanceState()`:
 * in late lifecycle methods, or in a lambda / anonymous-class callback with no `isStateSaved()` guard.
 */
class CommitAfterStateSavedDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String> = listOf("commit", "commitNow")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!context.evaluator.isMemberInClass(method, FRAGMENT_TRANSACTION_CLASS)) return

        var inCallback = false
        var outerMethod: UMethod? = null
        var parent: UElement? = node.uastParent
        while (parent != null) {
            if (parent is ULambdaExpression) inCallback = true
            if (parent is UMethod) {
                val owner = parent.uastParent as? UClass
                if (owner != null && owner.javaPsi is PsiAnonymousClass) {
                    inCallback = true
                } else {
                    outerMethod = parent
                    break
                }
            }
            parent = parent.uastParent
        }

        val inLateLifecycle = outerMethod?.name in LATE_LIFECYCLE_METHODS
        if (!inLateLifecycle && !inCallback) return
        if (!inLateLifecycle && outerMethod != null && checksStateSaved(outerMethod)) return

        val where = if (inLateLifecycle) "in `${outerMethod?.name}()`" else "from an asynchronous callback"
        context.report(
            ISSUE, node, context.getLocation(node),
            "`${method.name}()` called $where can run after `onSaveInstanceState()` and throw " +
                "`IllegalStateException`; guard it with `isStateSaved()`"
        )
    }

    private fun checksStateSaved(method: UMethod): Boolean {
        var found = false
        method.accept(object : AbstractUastVisitor() {
            override fun visitCallExpression(node: UCallExpression): Boolean {
                if (node.methodName == "isStateSaved") found = true
                return super.visitCallExpression(node)
            }

            // Kotlin property syntax: fragmentManager.isStateSaved
            override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression): Boolean {
                if (node.identifier == "isStateSaved") found = true
                return super.visitSimpleNameReferenceExpression(node)
            }
        })
        return found
    }

    companion object {
        private val LATE_LIFECYCLE_METHODS = setOf("onSaveInstanceState", "onStop", "onDestroy")

        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "UhcCommitAfterStateSaved",
            briefDescription = "Fragment transaction may be committed after onSaveInstanceState()",
            explanation = """
                Once `onSaveInstanceState()` has run, `FragmentTransaction.commit()` throws \
                `IllegalStateException: Can not perform this action after onSaveInstanceState`. \
                This happens when a transaction is committed from `onStop()`/`onDestroy()`, or from \
                a callback (click handler posted with a delay, network response, timer) that fires \
                after the activity has gone to the background.

                Check `getSupportFragmentManager().isStateSaved()` before committing from callbacks. \
                Use `commitAllowingStateLoss()` only where losing the transaction on process death \
                is acceptable.
            """,
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(CommitAfterStateSavedDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
