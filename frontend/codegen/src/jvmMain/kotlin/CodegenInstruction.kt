package org.plank.codegen

import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.Value

interface CodegenInstruction {
  fun CodegenCtx.codegen(): Value
}
