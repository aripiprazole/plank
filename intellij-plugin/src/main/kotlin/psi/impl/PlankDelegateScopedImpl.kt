package com.lorenzoog.jplank.intellijplugin.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.lorenzoog.jplank.analyzer.Variable
import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.intellijplugin.analyzer.LookupResult
import com.lorenzoog.jplank.intellijplugin.analyzer.PsiBindingContext
import com.lorenzoog.jplank.intellijplugin.analyzer.Scope
import com.lorenzoog.jplank.intellijplugin.psi.findScope

open class PlankDelegateScopedImpl(node: ASTNode) : ASTWrapperPsiElement(node), Scope {
  private val context by lazy {
    PsiBindingContext(findScope()!!)
  }

  private val delegate by lazy {
    context.visit(this)

    context.topScope
  }

  override fun getContext(): PsiElement? {
    return parent
  }

  override fun expand(another: Scope): Scope {
    return delegate.expand(another)
  }

  override fun declare(name: String, type: PlankType, mutable: Boolean) {
    return delegate.declare(name, type, mutable)
  }

  override fun define(name: String, type: PlankType.Struct) {
    return delegate.define(name, type)
  }

  override fun findType(name: String): PlankType? {
    return delegate.findType(name)
  }

  override fun findStruct(name: String): PlankType.Struct? {
    return delegate.findStruct(name)
  }

  override fun findVariable(name: String): Variable? {
    return delegate.findVariable(name)
  }

  override fun findFunction(name: String): PlankType.Callable? {
    return delegate.findFunction(name)
  }

  override fun lookup(): List<LookupResult> {
    return delegate.lookup()
  }
}
