package com.lorenzoog.jplank.intellijplugin.psi

import com.intellij.psi.PsiElement
import com.lorenzoog.jplank.intellijplugin.analyzer.Scope

tailrec fun PsiElement.findScope(): Scope? {
  if (parent is PsiScope) {
    return parent as PsiScope
  }

  return parent?.findScope()
}

