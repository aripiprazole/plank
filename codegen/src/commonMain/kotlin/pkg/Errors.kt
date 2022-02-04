package org.plank.codegen.pkg

import org.plank.analyzer.BindingViolation
import org.plank.syntax.mapper.SyntaxViolation

class SyntaxError(val violations: Set<SyntaxViolation>) : RuntimeException()

class AnalyzerError(val violations: Set<BindingViolation>) : RuntimeException()
