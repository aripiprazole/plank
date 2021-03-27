package com.lorenzoog.plank.cli.compiler

import com.lorenzoog.plank.analyzer.BindingViolation
import com.lorenzoog.plank.grammar.element.PlankElement
import com.lorenzoog.plank.grammar.mapper.SyntaxViolation
import org.llvm4j.llvm4j.Module

sealed class CompileError : Throwable() {
  class BindingViolations(val violations: List<BindingViolation>) : CompileError()
  class SyntaxViolations(val violations: List<SyntaxViolation>) : CompileError()
  class FailedCommand(val command: String, val exitCode: Int) : CompileError()
  class IRViolations(
    val module: Module,
    val violations: Map<PlankElement?, String>
  ) : CompileError()
}
