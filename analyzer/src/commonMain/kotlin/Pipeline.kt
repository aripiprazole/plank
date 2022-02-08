package org.plank.analyzer

import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.phases.AnalyzingPhase
import org.plank.syntax.element.PlankFile

/**
 * Analyzes the provided [PlankFile] and returns a typed [ResolvedPlankFile]
 * with typed declarations/statements/expressions.
 */
fun analyze(file: PlankFile, tree: ModuleTree): ResolvedPlankFile {
  return AnalyzingPhase(tree).analyze(file)
}
