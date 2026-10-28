package com.example.uhc.lint

import org.jetbrains.uast.UBinaryExpressionWithType
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UParenthesizedExpression
import org.jetbrains.uast.UQualifiedReferenceExpression

internal const val FRAGMENT_CLASS = "androidx.fragment.app.Fragment"
internal const val FRAGMENT_TRANSACTION_CLASS = "androidx.fragment.app.FragmentTransaction"

/**
 * True when the value returned by this call is used as the receiver of another call or
 * field access, e.g. `getActivity().getTitle()` or `((MainActivity) getActivity()).foo()`.
 * Those are the places a null return value turns into a NullPointerException.
 */
internal fun UCallExpression.isDereferenced(): Boolean {
    var expr: UElement = this
    var parent = expr.uastParent
    // `this.getActivity()`: step out to the whole qualified expression.
    if (parent is UQualifiedReferenceExpression && parent.selector.sourcePsi == expr.sourcePsi) {
        expr = parent
        parent = parent.uastParent
    }
    while (parent is UParenthesizedExpression || parent is UBinaryExpressionWithType) {
        expr = parent
        parent = parent.uastParent
    }
    return parent is UQualifiedReferenceExpression && parent.receiver.sourcePsi == expr.sourcePsi
}
