package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.Expr.Logical.Operation
import io.vexelabs.bitbuilder.llvm.ir.RealPredicate
import io.vexelabs.bitbuilder.llvm.ir.Value

class LogicalInstruction(private val descriptor: Expr.Logical) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val lhs = context.map(descriptor.lhs).codegen(context)
      ?: return context.report("lhs is null", descriptor)

    val rhs = context.map(descriptor.rhs).codegen(context)
      ?: return context.report("rhs is null", descriptor)

    return when (descriptor.op) {
      Operation.Equals -> {
        if (Builtin.Numeric.isAssignableBy(context.binding.visit(descriptor.rhs))) {
          context.builder.createFCmp(lhs, RealPredicate.OEQ, rhs, "fcmptmp")
        } else {
          val function = context.runtime.eqFunction
            ?: return context.report("eq function is null", descriptor)

          context.builder.createCall(function, listOf(lhs, rhs), "eqtmp")
        }
      }
      Operation.NotEquals -> {
        if (Builtin.Numeric.isAssignableBy(context.binding.visit(descriptor.rhs))) {
          context.builder.createFCmp(lhs, RealPredicate.ONE, rhs, "fcmptmp")
        } else {
          val function = context.runtime.neqFunction
            ?: return context.report("neq function is null", descriptor)

          context.builder.createCall(function, listOf(lhs, rhs), "neqtmp")
        }
      }
      Operation.Greater -> context.builder.createFCmp(lhs, RealPredicate.OGT, rhs, "fcmptmp")
      Operation.GreaterEquals -> context.builder.createFCmp(lhs, RealPredicate.OGE, rhs, "fcmptmp")
      Operation.Less -> context.builder.createFCmp(lhs, RealPredicate.OLT, rhs, "fcmptmp")
      Operation.LessEquals -> context.builder.createFCmp(lhs, RealPredicate.OGE, rhs, "fcmptmp")
    }
  }
}
