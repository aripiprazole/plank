package org.plank.compiler.pkg

import org.plank.analyzer.BindingViolation
import org.plank.grammar.mapper.SyntaxViolation

class SyntaxError(val violations: Set<SyntaxViolation>) : RuntimeException()

class AnalyzerError(val violations: Set<BindingViolation>) : RuntimeException()
