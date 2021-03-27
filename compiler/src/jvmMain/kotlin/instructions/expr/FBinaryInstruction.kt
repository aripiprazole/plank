package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Expr.Binary.Operation
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.None
import org.llvm4j.optional.Some

class FBinaryInstruction(val descriptor: Expr.Binary) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val lhs = context.map(descriptor.lhs).codegen(context)
      ?: return context.report("lhs is null", descriptor)

    val rhs = context.map(descriptor.rhs).codegen(context)
      ?: return context.report("rhs is null", descriptor)

    if (descriptor.op == Operation.Concat) {
      val concatFunction = context.runtime.concatFunction
        ?: return context.report("concat function is null", descriptor)

      return context.builder.buildCall(concatFunction, lhs, rhs, name = None)
    }

    val frhs = context.dataTypeConverter.convertToFloat(context, rhs)
    val flhs = context.dataTypeConverter.convertToFloat(context, lhs)

    return when (descriptor.op) {
      Operation.Sub -> context.builder.buildFloatSub(frhs, flhs, Some("subtmp"))
      Operation.Mul -> context.builder.buildFloatMul(flhs, frhs, Some("multmp"))
      Operation.Div -> context.builder.buildFloatDiv(flhs, frhs, Some("divtmp"))
      Operation.Add -> context.builder.buildFloatAdd(flhs, frhs, Some("addtmp"))
      Operation.Concat -> error("The could should never reach here.")
    }
  }
}
