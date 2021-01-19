package com.lorenzoog.jplank.element

import org.antlr.v4.kotlinruntime.Token

sealed class ImportDirective : PlankElement {
  interface Visitor<T> {
    fun visit(importDirective: ImportDirective): T = importDirective.accept(this)

    fun visitModuleImportDirective(module: Module): T
  }

  abstract fun <T> accept(visitor: Visitor<T>): T

  class Module(val name: Token, override val location: Location) : ImportDirective() {
    override fun <T> accept(visitor: Visitor<T>): T {
      return visitor.visitModuleImportDirective(this)
    }
  }
}
