package com.lorenzoog.jplank.intellijplugin.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.lorenzoog.jplank.intellijplugin.psi.PlankNamedElement
import com.lorenzoog.jplank.intellijplugin.psi.PlankTypes

open class PlankNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), PlankNamedElement {
  private val primary by lazy {
    findChildByType<LeafPsiElement>(PlankTypes.IDENTIFIER)
  }

  override fun getName(): String {
    return primary?.text!!
  }

  override fun setName(name: String): PsiElement {
    if (primary == null) {
      return this
    }

    return primary!!.replace(ASTWrapperPsiElement(primary!!.replaceWithText(name)))
  }
}
