package org.plank.codegen

import org.plank.codegen.scope.CodegenContext
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.Value

actual fun CodegenContext.unsafeAlloca(value: Value): AllocaInst {
  return AllocaInst(value.ref)
}

actual fun CodegenContext.unsafeFunction(value: Value): Function {
  return Function(value.ref)
}
