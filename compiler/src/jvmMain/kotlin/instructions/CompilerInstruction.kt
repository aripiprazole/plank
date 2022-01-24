package com.gabrielleeg1.plank.compiler.instructions

import com.gabrielleeg1.plank.compiler.CompilerContext
import org.llvm4j.llvm4j.Value

interface CompilerInstruction {
  fun CompilerContext.codegen(): Value
}
