package com.lorenzoog.jplank.intellijplugin.psi

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.PlatformIcons

class PlankReference(name: PlankNamedElement) : PsiReferenceBase<PlankNamedElement>(name) {
  override fun resolve(): PsiElement? {
    return myElement
  }

  override fun getVariants(): Array<Any> {
    return arrayOf(
      LookupElementBuilder
        .create(myElement.name.toString())
        .withIcon(PlatformIcons.FUNCTION_ICON)
    )
  }
}
