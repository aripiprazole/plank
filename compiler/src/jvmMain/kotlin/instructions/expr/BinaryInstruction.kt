package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Expr.Binary.Operation
import org.llvm4j.llvm4j.Value
import org.llvm4j.llvm4j.WrapSemantics
import org.llvm4j.optional.None
import org.llvm4j.optional.Some

class BinaryInstruction(val descriptor: Expr.Binary) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val lhs = context.map(descriptor.lhs).codegen(context)
      ?: return context.report("lhs is null", descriptor)

    val rhs = context.map(descriptor.rhs).codegen(context)
      ?: return context.report("rhs is null", descriptor)

    return when (descriptor.op) {
      Operation.Sub -> {
        context.builder.buildIntSub(rhs, lhs, WrapSemantics.Unspecified, Some("subtmp"))
      }

      Operation.Mul -> {
        context.builder.buildIntMul(lhs, rhs, WrapSemantics.Unspecified, Some("multmp"))
      }

      Operation.Div -> {
        val frhs = context.dataTypeConverter.convertToFloat(context, rhs)
        val flhs = context.dataTypeConverter.convertToFloat(context, lhs)

        context.builder.buildFloatDiv(flhs, frhs, Some("divtmp"))
      }

      Operation.Add -> {
        context.builder.buildIntAdd(lhs, rhs, WrapSemantics.Unspecified, Some("addtmp"))
      }

      Operation.Concat -> {
        val concatFunction = context.runtime.concatFunction
          ?: return context.report("concat function is null", descriptor)

        return context.builder.buildCall(concatFunction, lhs, rhs, name = None)
      }
    }
  }
}
