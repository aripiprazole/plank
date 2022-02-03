package org.plank.compiler

import org.plank.llvm4k.ir.Value

interface CodegenInstruction {
  fun CodegenContext.codegen(): Value
}
