package org.plank.analyzer

import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.phases.AnalyzingPhase
import org.plank.analyzer.phases.InliningPhase
import org.plank.syntax.element.PlankFile

/**
 * Analyzes the provided [PlankFile] and returns a typed [ResolvedPlankFile]
 * with typed declarations/statements/expressions.
 */
fun analyze(file: PlankFile): ResolvedPlankFile {
  return AnalyzingPhase()
    .visitPlankFile(file)
    .transform(InliningPhase)
}
