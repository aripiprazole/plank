package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.llvm.buildAdd
import com.lorenzoog.jplank.compiler.llvm.buildCall
import com.lorenzoog.jplank.compiler.llvm.buildFDiv
import com.lorenzoog.jplank.compiler.llvm.buildMul
import com.lorenzoog.jplank.compiler.llvm.buildSub
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.Expr.Binary.Operation
import org.llvm4j.llvm4j.Value

class BinaryInstruction(val descriptor: Expr.Binary) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val lhs = context.map(descriptor.lhs).codegen(context)
      ?: return context.report("lhs is null", descriptor)

    val rhs = context.map(descriptor.rhs).codegen(context)
      ?: return context.report("rhs is null", descriptor)

    return when (descriptor.op) {
      Operation.Sub -> context.builder.buildSub(rhs, lhs, "subtmp")
      Operation.Mul -> context.builder.buildMul(lhs, rhs, "multmp")
      Operation.Div -> {
        val frhs = context.dataTypeConverter.convertToFloat(context, rhs)
        val flhs = context.dataTypeConverter.convertToFloat(context, lhs)

        context.builder.buildFDiv(flhs, frhs, "divtmp")
      }
      Operation.Add -> context.builder.buildAdd(lhs, rhs, "addtmp")
      Operation.Concat -> {
        val concatFunction = context.runtime.concatFunction
          ?: return context.report("concat function is null", descriptor)

        return context.builder.buildCall(concatFunction, listOf(lhs, rhs))
      }
    }
  }
}
