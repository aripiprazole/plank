package org.plank.codegen.type

import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.Type

sealed interface CodegenType {
  fun CodegenCtx.get(): Type

  fun CodegenCtx.declare()

  fun CodegenCtx.codegen()
}
