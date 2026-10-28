package com.example.uhc.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.ULiteralExpression
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UastBinaryOperator
import org.jetbrains.uast.skipParenthesizedExprDown
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * Flags View / ViewBinding fields in a Fragment that are not set to null in `onDestroyView()`.
 * The fragment outlives its view (back stack, ViewPager), so these fields keep a destroyed
 * view hierarchy alive and invite access after `onDestroyView()`.
 */
class ViewAccessAfterDestroyViewDetector : Detector(), SourceCodeScanner {

    override fun applicableSuperClasses(): List<String> = listOf(FRAGMENT_CLASS)

    override fun visitClass(context: JavaContext, declaration: UClass) {
        if (declaration.qualifiedName == FRAGMENT_CLASS) return

        val viewFields = declaration.fields.filter {
            !context.evaluator.isStatic(it) && isViewType(context, it.type)
        }
        if (viewFields.isEmpty()) return

        val cleared = mutableSetOf<String>()
        declaration.methods
            .firstOrNull { it.name == "onDestroyView" && it.uastParameters.isEmpty() }
            ?.uastBody
            ?.accept(object : AbstractUastVisitor() {
                override fun visitBinaryExpression(node: UBinaryExpression): Boolean {
                    val value = node.rightOperand.skipParenthesizedExprDown()
                    if (node.operator == UastBinaryOperator.ASSIGN && value is ULiteralExpression && value.isNull) {
                        assignedName(node)?.let { cleared += it }
                    }
                    return super.visitBinaryExpression(node)
                }
            })

        for (field in viewFields) {
            if (field.name in cleared) continue
            context.report(
                ISSUE, field, context.getNameLocation(field),
                "View field `${field.name}` is not cleared in `onDestroyView()`; set it to null there " +
                    "or look the view up from `getView()` when needed"
            )
        }
    }

    private fun assignedName(node: UBinaryExpression): String? =
        when (val target = node.leftOperand.skipParenthesizedExprDown()) {
            is USimpleNameReferenceExpression -> target.identifier
            is UQualifiedReferenceExpression -> (target.selector as? USimpleNameReferenceExpression)?.identifier
            else -> null
        }

    private fun isViewType(context: JavaContext, type: PsiType): Boolean {
        val cls = (type as? PsiClassType)?.resolve() ?: return false
        return context.evaluator.extendsClass(cls, "android.view.View", false) ||
            context.evaluator.inheritsFrom(cls, "androidx.viewbinding.ViewBinding", false)
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "UhcViewAccessAfterDestroyView",
            briefDescription = "Fragment keeps view references after onDestroyView()",
            explanation = """
                A fragment's view is destroyed in `onDestroyView()` while the fragment object itself \
                stays alive (for example on the back stack). Fields that hold Views or a ViewBinding \
                leak the old view hierarchy, and code that touches them later (callbacks, \
                `TextWatcher`s, async results) works on a dead view or throws \
                `IllegalStateException` from `getViewLifecycleOwner()`.

                Set such fields to null in `onDestroyView()`, or avoid storing views in fields.
            """,
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(ViewAccessAfterDestroyViewDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
