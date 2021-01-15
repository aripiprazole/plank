package com.lorenzoog.jplank.compiler.runtime

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.expr.ReferenceInstruction
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Context
import io.vexelabs.bitbuilder.llvm.ir.Module
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue

class PlankRuntime(
  private val builder: Builder,
  private val module: Module
) {
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

  private val plankCreateObjectFunction by lazy {
    val context = module.getContext()
    val type = context.getFunctionType(
      types.any.getPointerType(),
      types.string,
      types.voidPtr,
      variadic = false
    )

    module.createFunction("Plank_Create_Object", type)
  }

  fun createObject(context: PlankContext, descriptor: Expr): Value? {
    val value = context.map(descriptor).codegen(context) ?: return null
    val type = context.binding.visit(descriptor)

    val v = if (type.isPrimitive) {
      val ref = ReferenceInstruction.getReference(context, descriptor)
        ?: return context.report("failed to get reference of descriptor", descriptor)

      context.builder.createBitCast(ref, types.voidPtr, "bitcasttmp")
    } else {
      value
    }

    return builder.createCall(
      plankCreateObjectFunction,
      listOf(builder.buildGlobalString(type.toString(), "objtmptype", true), v),
    )
  }

  class Types(context: Context) {
    val i1 = context.getIntType(1)
    val i8 = context.getIntType(8)
    val i16 = context.getIntType(16)
    val int = context.getIntType(64)
    val double = context.getFloatType(TypeKind.Double)
    val float = context.getFloatType(TypeKind.Float)
    val string = i8.getPointerType()
    val void = context.getVoidType()
    val voidPtr = i8.getPointerType()

    val any: StructType = context.getOpaqueStructType("Object").also { struct ->
      context.getFunctionType(string, struct.getPointerType(), variadic = false).also { toString ->
        struct.setBody(
          types = listOf(string, int, voidPtr, toString.getPointerType()),
          packed = false
        )
      }
    }
  }

  companion object {
    const val EQ_CALL = "Plank_Builtin_eq"
    const val NEQ_CALL = "Plank_Builtin_neq"
    const val CONCAT_CALL = "Plank_Builtin_concat"
  }
}
