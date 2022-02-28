package org.plank.analyzer.element

import org.plank.analyzer.AnalyzerViolation
import org.plank.analyzer.resolver.Module
import org.plank.analyzer.resolver.ModuleTree
import org.plank.syntax.debug.DontDump
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.Loc
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.mapper.SyntaxViolation

/**
 * Represents a [PlankFile] with type definitions. The properties [syntaxViolations],
 * [analyzerViolations], [dependencies] will be fulfilled by copy the generated instances
 */
data class ResolvedPlankFile(
  val program: List<ResolvedDecl>,
  val fileModule: Module,
  val tree: ModuleTree,
  @DontDump val delegate: PlankFile,
  @DontDump val syntaxViolations: List<SyntaxViolation> = delegate.violations,
  @DontDump val analyzerViolations: List<AnalyzerViolation> = emptyList(),
  @DontDump val dependencies: List<ResolvedPlankFile> = emptyList(),
) : ResolvedPlankElement {
  interface Visitor<T> {
    fun visitPlankFile(file: ResolvedPlankFile): T
  }

  val module: Identifier = delegate.module
  val moduleName: QualifiedPath? = delegate.moduleName
  val path: String = delegate.path

  override val loc: Loc = delegate.loc
}
