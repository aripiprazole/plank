package com.lorenzoog.jplank.intellijplugin.psi

import com.intellij.psi.PsiNamedElement

interface PlankNamedElement : PsiNamedElement {
  override fun getName(): String
}
