package org.plank.analyzer.element

import org.plank.analyzer.AnalyzerViolation
import org.plank.analyzer.infer.Module
import org.plank.analyzer.infer.ModuleTree
import org.plank.analyzer.phases.IrTransformingPhase
import org.plank.syntax.debug.DontDump
import org.plank.syntax.element.PlankFile
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

  val module = delegate.module
  val moduleName = delegate.moduleName
  val path = delegate.path

  override val location = delegate.location

  fun transform(irTransformingPhase: IrTransformingPhase): ResolvedPlankFile {
    return irTransformingPhase.visitPlankFile(this)
  }
}
