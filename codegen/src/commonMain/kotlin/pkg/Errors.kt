package org.plank.codegen.pkg

import org.plank.analyzer.checker.CheckViolation
import org.plank.syntax.SyntaxViolation

class SyntaxError(val violations: Set<SyntaxViolation>) : RuntimeException()

class AnalyzerError(val violations: Set<CheckViolation>) : RuntimeException()
