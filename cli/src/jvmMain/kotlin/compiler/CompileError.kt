package com.gabrielleeg1.plank.cli.compiler

import com.gabrielleeg1.plank.analyzer.BindingViolation
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.grammar.mapper.SyntaxViolation
import org.llvm4j.llvm4j.Module

sealed class CompileError : Throwable() {
  class BindingViolations(val violations: List<BindingViolation>) : CompileError()
  class SyntaxViolations(val violations: List<SyntaxViolation>) : CompileError()
  class FailedCommand(val command: String, val exitCode: Int) : CompileError()
  class IRViolations(val module: Module, val violations: List<CodegenError>) : CompileError()
}
