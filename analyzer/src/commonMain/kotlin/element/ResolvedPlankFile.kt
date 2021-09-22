package com.lorenzoog.plank.analyzer.element

import com.lorenzoog.plank.analyzer.BindingContext
import com.lorenzoog.plank.analyzer.BindingViolation
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.mapper.SyntaxViolation

/**
 * Represents a [PlankFile] with type definitions. The properties [syntaxViolations],
 * [bindingViolations], [dependencies] will be fulfilled by copy the generated instances in
 * [BindingContext]
 */
data class ResolvedPlankFile(
  val delegate: PlankFile,
  val program: List<ResolvedDecl>,
  val syntaxViolations: List<SyntaxViolation> = emptyList(),
  val bindingViolations: List<BindingViolation> = emptyList(),
  val dependencies: List<ResolvedPlankFile> = emptyList(),
) : ResolvedPlankElement {
  interface Visitor<T> {
    fun visit(file: ResolvedPlankFile): T = visitPlankFile(file)

    fun visitPlankFile(file: ResolvedPlankFile): T
  }

  val moduleName = delegate.moduleName
  val path = delegate.path
  val realFile = delegate.realFile

  val isValid = delegate.isValid && bindingViolations.isEmpty()

  override val location = delegate.location
}
