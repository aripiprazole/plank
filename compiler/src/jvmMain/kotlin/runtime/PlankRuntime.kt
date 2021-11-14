package com.gabrielleeg1.plank.compiler.runtime

import org.llvm4j.llvm4j.Context
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.Linkage
import org.llvm4j.llvm4j.Module
import org.llvm4j.llvm4j.Value

class PlankRuntime(private val module: Module) {
  val types = Types(module.getContext())

  val trueConstant: Value = types.i1.getConstant(1)
  val falseConstant: Value = types.i1.getConstant(0)
  val nullConstant: Value = types.i1.getConstantNull()

  val printf: Function by lazy {
    val type = module.getContext().getFunctionType(types.int, types.string, isVariadic = true)
    val function = module.addFunction("printf", type)

    function.setLinkage(Linkage.External)

    function
  }

  val concatFunction: Function?
    get() {
      return module.getFunction(CONCAT_CALL).toNullable()
    }

  val eqFunction: Function?
    get() {
      return module.getFunction(EQ_CALL).toNullable()
    }

  val neqFunction: Function?
    get() {
      return module.getFunction(NEQ_CALL).toNullable()
    }

  class Types(context: Context) {
    val i1 = context.getInt1Type()
    val tag = context.getInt8Type()
    val i8 = context.getInt8Type()
    val i16 = context.getInt16Type()
    val int = context.getInt32Type()
    val double = context.getDoubleType()
    val float = context.getFloatType()
    val string = context.getPointerType(i8).unwrap()
    val void = context.getVoidType()
    val voidPtr = context.getPointerType(i8).unwrap()
  }

  companion object {
    const val EQ_CALL = "Plank_Internal_eq"
    const val NEQ_CALL = "Plank_Internal_neq"
    const val CONCAT_CALL = "internal_Plank_Internal_concat"
  }
}
