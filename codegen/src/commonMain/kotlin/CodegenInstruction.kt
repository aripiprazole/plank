package org.plank.codegen

import org.plank.codegen.scope.CodegenContext
import org.plank.llvm4k.ir.Value

interface CodegenInstruction {
  fun CodegenContext.codegen(): Value
}
