package com.lorenzoog.jplank.intellijplugin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.lorenzoog.jplank.intellijplugin.psi.impl.PlankNamedElementImpl

class PlankDotQualifiedExpr(val receiver: PsiElement, node: ASTNode) : PlankNamedElementImpl(node) {
  override fun getName(): String {
    return node.text
  }
}
