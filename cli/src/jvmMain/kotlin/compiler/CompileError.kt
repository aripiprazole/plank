package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.analyzer.BindingViolation
import com.lorenzoog.jplank.element.PlankElement
import com.lorenzoog.jplank.grammar.SyntaxViolation
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
