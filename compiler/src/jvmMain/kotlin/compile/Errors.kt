package com.gabrielleeg1.plank.compiler.compile

import com.gabrielleeg1.plank.analyzer.BindingViolation
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.grammar.mapper.SyntaxViolation
import org.llvm4j.llvm4j.Module
import java.lang.RuntimeException

class BindingError(val violations: List<BindingViolation>) : RuntimeException()
class SyntaxError(val violations: List<SyntaxViolation>) : RuntimeException()
class FailedCommand(val command: String, val exitCode: Int) : RuntimeException()
class IRDumpError(val module: Module, val violations: List<CodegenViolation>) : RuntimeException()
