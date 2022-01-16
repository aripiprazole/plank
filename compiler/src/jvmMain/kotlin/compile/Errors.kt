package com.gabrielleeg1.plank.compiler.compile

import com.gabrielleeg1.plank.analyzer.BindingViolation
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.grammar.mapper.SyntaxViolation
import org.llvm4j.llvm4j.Module

class BindingError(val violations: List<BindingViolation>) : RuntimeException()
class SyntaxError(val violations: List<SyntaxViolation>) : RuntimeException()
class FailedCommand(val command: String, val exitCode: Int) : RuntimeException() {
  override val message: String = "Command '$command' failed with exit code $exitCode"
}
class IRDumpError(val module: Module, val violations: List<CodegenViolation>) : RuntimeException()
