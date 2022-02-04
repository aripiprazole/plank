@file:JvmName("UtilsKtJvm")

package org.plank.codegen

import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.Value

actual fun CodegenContext.unsafeAlloca(value: Value): AllocaInst {
  TODO("Not yet implemented")
}

actual fun CodegenContext.unsafeFunction(value: Value): Function {
  TODO("Not yet implemented")
}
