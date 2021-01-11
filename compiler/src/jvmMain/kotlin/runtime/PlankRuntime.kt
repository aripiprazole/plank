package com.lorenzoog.jplank.compiler.runtime

import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue

class PlankRuntime(private val module: Module) {
  val types = Types(module.getContext())

  val trueConstant: Value = types.i1.getConstant(1)
  val falseConstant: Value = types.i1.getConstant(0)

  val concatFunction: FunctionValue?
    get() {
      return module.getFunction(CONCAT_CALL)
    }

  val eqFunction: FunctionValue?
    get() {
      return module.getFunction(EQ_CALL)
    }

  val neqFunction: FunctionValue?
    get() {
      return module.getFunction(NEQ_CALL)
    }

  class Types(context: Context) {
    val i1 = context.getIntType(1)
    val i8 = context.getIntType(8)
    val i16 = context.getIntType(16)
    val i32 = context.getIntType(32)
    val double = context.getFloatType(TypeKind.Double)
    val float = context.getFloatType(TypeKind.Float)
    val string = i8.getPointerType()
    val void = context.getVoidType()
  }

  companion object {
    const val EQ_CALL = "PLANK_BUILTIN_eq"
    const val NEQ_CALL = "PLANK_BUILTIN_neq"
    const val CONCAT_CALL = "PLANK_BUILTIN_concat"
  }
}
