package com.lorenzoog.jplank.intellijplugin.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.lorenzoog.jplank.analyzer.Variable
import com.lorenzoog.jplank.analyzer.PlankType
import com.lorenzoog.jplank.intellijplugin.Plank
import com.lorenzoog.jplank.intellijplugin.PlankFileType
import com.lorenzoog.jplank.intellijplugin.analyzer.CodeAnnotation
import com.lorenzoog.jplank.intellijplugin.analyzer.LookupResult
import com.lorenzoog.jplank.intellijplugin.analyzer.PsiBindingContext
import com.lorenzoog.jplank.intellijplugin.analyzer.Scope
import com.lorenzoog.plank.intellijplugin.psi.PlankDecl
import com.lorenzoog.plank.intellijplugin.psi.PlankImports

class PlankPsiFile(viewProvider: FileViewProvider) :
  PsiFileBase(viewProvider, Plank),
  PsiScope {
  private val delegate by lazy { context.topScope }

  private val context: PsiBindingContext = PsiBindingContext()

  val imports: PlankImports? = findChildByClass(PlankImports::class.java)
  val program: Array<PlankDecl> = findChildrenByClass(PlankDecl::class.java)!!

  var annotations: List<CodeAnnotation>
    private set

  fun update(): PlankPsiFile {
    return PlankPsiFile(viewProvider)
  }

  fun analyze(): List<CodeAnnotation> {
    context.annotations.clear()
    context.visit(this)
    annotations = context.annotations.toList()
    return annotations
  }

  init {
    context.visit(this)

    annotations = context.annotations.toList()
  }

  override fun getFileType(): FileType {
    return PlankFileType
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
