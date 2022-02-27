package org.plank.analyzer

import org.plank.analyzer.checker.typeCheck
import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.resolver.ModuleTree
import org.plank.analyzer.resolver.resolveImports
import org.plank.syntax.element.PlankFile

/**
 * Analyzes the provided [PlankFile] and returns a typed [ResolvedPlankFile]
 * with typed declarations/statements/expressions.
 */
fun analyze(file: PlankFile, tree: ModuleTree = ModuleTree()): ResolvedPlankFile {
  return resolveImports(file, tree).typeCheck()
}
