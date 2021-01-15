package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.Expr.Binary.Operation
import io.vexelabs.bitbuilder.llvm.ir.Value

class BinaryInstruction(val descriptor: Expr.Binary) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val lhs = context.map(descriptor.lhs).codegen(context)
      ?: return context.report("lhs is null", descriptor)

    val rhs = context.map(descriptor.rhs).codegen(context)
      ?: return context.report("rhs is null", descriptor)

    return when (descriptor.op) {
      Operation.Sub -> context.builder.createFSub(lhs, rhs, "subtmp")
      Operation.Mul -> context.builder.createFMul(lhs, rhs, "multmp")
      Operation.Div -> context.builder.createFDiv(lhs, rhs, "divtmp")
      Operation.Add -> if (Builtin.String.isAssignableBy(context.binding.visit(descriptor))) {
        val concatFunction = context.runtime.concatFunction
          ?: return context.report("concat function is null", descriptor)

        context.builder.createCall(concatFunction, listOf(lhs, rhs))
      } else {
        context.builder.createFAdd(lhs, rhs, "addtmp")
      }
    }
  }
}
