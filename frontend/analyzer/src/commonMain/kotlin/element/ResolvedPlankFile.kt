package org.plank.analyzer.element

import org.plank.analyzer.infer.InferViolation
import org.plank.analyzer.resolver.Module
import org.plank.analyzer.resolver.ModuleTree
import org.plank.syntax.SyntaxViolation
import org.plank.syntax.debug.DontDump
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Loc
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.QualifiedPath

/**
 * Represents a [PlankFile] with type definitions. The properties [syntaxViolations],
 * [inferViolations], [dependencies] will be fulfilled by copy the generated instances
 */
data class ResolvedPlankFile(
  val program: List<ResolvedDecl>,
  val fileModule: Module,
  val tree: ModuleTree,
  @DontDump val delegate: PlankFile,
  @DontDump val syntaxViolations: List<SyntaxViolation> = delegate.violations,
  @DontDump val inferViolations: List<InferViolation> = emptyList(),
  @DontDump val dependencies: List<ResolvedPlankFile> = emptyList(),
) : ResolvedPlankElement {
  val module: Identifier = delegate.module
  val moduleName: QualifiedPath? = delegate.moduleName
  val path: String = delegate.path

  override val loc: Loc = delegate.loc
}
