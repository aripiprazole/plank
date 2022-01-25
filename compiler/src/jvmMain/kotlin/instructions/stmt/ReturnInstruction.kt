package com.gabrielleeg1.plank.compiler.instructions.stmt

import com.gabrielleeg1.plank.analyzer.element.ResolvedReturnStmt
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.builder.buildReturnUnit
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class ReturnInstruction(private val descriptor: ResolvedReturnStmt) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    return when (val value = descriptor.value?.codegen()) {
      null -> buildReturnUnit()
      else -> buildReturn(value)
    }
  }
}
