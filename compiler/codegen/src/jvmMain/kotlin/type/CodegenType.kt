package org.plank.codegen.type

import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.nullSubst
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.Type

sealed interface CodegenType {
  fun CodegenCtx.get(subst: Subst = nullSubst()): Type

  fun CodegenCtx.genSubTypes(target: RankedType) {}

  fun CodegenCtx.declare()

  fun CodegenCtx.codegen()
}
