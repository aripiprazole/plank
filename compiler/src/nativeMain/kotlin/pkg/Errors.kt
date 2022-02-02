package com.gabrielleeg1.plank.compiler.pkg

import com.gabrielleeg1.plank.analyzer.BindingViolation
import com.gabrielleeg1.plank.grammar.mapper.SyntaxViolation

class SyntaxError(val violations: Set<SyntaxViolation>) : RuntimeException()

class AnalyzerError(val violations: Set<BindingViolation>) : RuntimeException()
