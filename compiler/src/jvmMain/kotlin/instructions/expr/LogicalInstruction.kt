package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.analyzer.Builtin
import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Expr.Logical.Operation
import org.llvm4j.llvm4j.FloatPredicate
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.Some

class LogicalInstruction(private val descriptor: Expr.Logical) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val lhs = context.map(descriptor.lhs).codegen(context)
      ?: return context.report("lhs is null", descriptor)

    val rhs = context.map(descriptor.rhs).codegen(context)
      ?: return context.report("rhs is null", descriptor)

    return when (descriptor.op) {
      Operation.Equals -> {
        if (Builtin.Numeric.isAssignableBy(context.binding.visit(descriptor.rhs))) {
          context.builder
            .buildFloatCompare(FloatPredicate.OrderedEqual, lhs, rhs, Some("fcmptmp"))
        } else {
          val function = context.runtime.eqFunction
            ?: return context.report("eq function is null", descriptor)

          context.builder.buildCall(function, lhs, rhs, name = Some("eqtmp"))
        }
      }

      Operation.NotEquals -> {
        if (Builtin.Numeric.isAssignableBy(context.binding.visit(descriptor.rhs))) {
          context.builder
            .buildFloatCompare(FloatPredicate.OrderedNotEqual, lhs, rhs, Some("fcmptmp"))
        } else {
          val function = context.runtime.neqFunction
            ?: return context.report("neq function is null", descriptor)

          context.builder.buildCall(function, lhs, rhs, name = Some("neqtmp"))
        }
      }

      Operation.Greater -> {
        context.builder
          .buildFloatCompare(FloatPredicate.OrderedGreaterThan, lhs, rhs, Some("fcmptmp"))
      }

      Operation.GreaterEquals -> {
        context.builder
          .buildFloatCompare(FloatPredicate.OrderedGreaterEqual, lhs, rhs, Some("fcmptmp"))
      }

      Operation.Less -> {
        context.builder
          .buildFloatCompare(FloatPredicate.OrderedLessThan, lhs, rhs, Some("fcmptmp"))
      }

      Operation.LessEquals -> {
        context.builder
          .buildFloatCompare(FloatPredicate.OrderedLessEqual, lhs, rhs, Some("fcmptmp"))
      }
    }
  }
}
