package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.Builtin
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildFCmp
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.Expr.Logical.Operation
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either
import org.llvm4j.llvm4j.FloatPredicate

class LogicalInstruction(private val descriptor: Expr.Logical) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val lhs = !descriptor.lhs.toInstruction().codegen()
    val rhs = !descriptor.rhs.toInstruction().codegen()

    Right(
      when (descriptor.op) {
        Operation.Equals -> {
          if (Builtin.Numeric.isAssignableBy(binding.visit(descriptor.rhs))) {
            buildFCmp(FloatPredicate.OrderedEqual, lhs, rhs, "fcmp.tmp")
          } else {
            TODO()
          }
        }

        Operation.NotEquals -> {
          if (Builtin.Numeric.isAssignableBy(binding.visit(descriptor.rhs))) {
            buildFCmp(FloatPredicate.OrderedNotEqual, lhs, rhs, "fcmp.tmp")
          } else {
            TODO()
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
