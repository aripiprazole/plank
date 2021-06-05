package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.BindingViolation
import com.lorenzoog.plank.analyzer.BindingContext
import com.lorenzoog.plank.grammar.element.PlankElement
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.mapper.SyntaxViolation

/**
 * Represents a [PlankFile] with type definitions. The properties [syntaxViolations],
 * [bindingViolations], [dependencies] will be fulfilled by copy the generated instances in
 * [BindingContext]
 */
data class TypedPlankFile(
  val delegate: PlankFile,
  val program: List<TypedDecl>,
  val syntaxViolations: List<SyntaxViolation> = emptyList(),
  val bindingViolations: List<BindingViolation> = emptyList(),
  val dependencies: List<TypedPlankFile> = emptyList(),
) : PlankElement {
  interface Visitor<T> {
    fun visit(file: TypedPlankFile): T = visitPlankFile(file)

    fun visitPlankFile(file: TypedPlankFile): T
  }

  val moduleName = delegate.moduleName
  val path = delegate.path
  val realFile = delegate.realFile

  val isValid = delegate.isValid && bindingViolations.isEmpty()

  override val location = delegate.location
}
