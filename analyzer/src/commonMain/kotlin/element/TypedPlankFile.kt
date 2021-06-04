package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.BindingViolation
import com.lorenzoog.plank.grammar.element.PlankFile

data class TypedPlankFile(
  val program: List<TypedDecl>,
  val bindingViolations: List<BindingViolation>,
  val delegate: PlankFile,
) : TypedPlankElement {
  interface Visitor<T> {
    fun visit(file: TypedPlankFile): T = visitPlankFile(file)

    fun visitPlankFile(file: TypedPlankFile): T
  }

  val moduleName = delegate.moduleName
  val path = delegate.path
  val violations = delegate.violations
  val realFile = delegate.realFile

  val isValid = delegate.isValid && bindingViolations.isEmpty()

  override val location = delegate.location
}
