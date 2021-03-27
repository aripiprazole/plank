package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.analyzer.Builtin
import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildCall
import com.lorenzoog.plank.compiler.buildFCmp
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Expr.Logical.Operation
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.FloatPredicate

class LogicalInstruction(private val descriptor: Expr.Logical) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val lhs = !descriptor.lhs.toInstruction().codegen()
    val rhs = !descriptor.rhs.toInstruction().codegen()

    Right(
      when (descriptor.op) {
        Operation.Equals -> {
          if (Builtin.Numeric.isAssignableBy(binding.visit(descriptor.rhs))) {
            buildFCmp(FloatPredicate.OrderedEqual, lhs, rhs, "fcmp.tmp")
          } else {
            val function = runtime.eqFunction
              ?: return Left("eq function is null")

            buildCall(function, listOf(lhs, rhs), "eq.tmp")
          }
        }

        Operation.NotEquals -> {
          if (Builtin.Numeric.isAssignableBy(binding.visit(descriptor.rhs))) {
            buildFCmp(FloatPredicate.OrderedNotEqual, lhs, rhs, "fcmp.tmp")
          } else {
            val function = runtime.neqFunction
              ?: return Left("neq function is null")

            buildCall(function, listOf(lhs, rhs), "neq.tmp")
          }
        }

        Operation.Greater -> {
          buildFCmp(FloatPredicate.OrderedGreaterThan, lhs, rhs, "fcmp.tmp")
        }

        Operation.GreaterEquals -> {
          buildFCmp(FloatPredicate.OrderedGreaterEqual, lhs, rhs, "fcmp.tmp")
        }

        Operation.Less -> {
          buildFCmp(FloatPredicate.OrderedLessThan, lhs, rhs, "fcmp.tmp")
        }

        Operation.LessEquals -> {
          buildFCmp(FloatPredicate.OrderedLessEqual, lhs, rhs, "fcmp.tmp")
        }
      }
    )
  }
}
