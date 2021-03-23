package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.llvm.buildCall
import com.lorenzoog.jplank.compiler.llvm.buildFCmp
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.Expr.Logical.Operation
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.Value

class LogicalInstruction(private val descriptor: Expr.Logical) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val lhs = context.map(descriptor.lhs).codegen(context)
      ?: return context.report("lhs is null", descriptor)

    val rhs = context.map(descriptor.rhs).codegen(context)
      ?: return context.report("rhs is null", descriptor)

    return when (descriptor.op) {
      Operation.Equals -> {
        if (Builtin.Numeric.isAssignableBy(context.binding.visit(descriptor.rhs))) {
          context.builder.buildFCmp(LLVM.LLVMRealOEQ, lhs, rhs, "fcmptmp")
        } else {
          val function = context.runtime.eqFunction
            ?: return context.report("eq function is null", descriptor)

          context.builder.buildCall(function, listOf(lhs, rhs), "eqtmp")
        }
      }
      Operation.NotEquals -> {
        if (Builtin.Numeric.isAssignableBy(context.binding.visit(descriptor.rhs))) {
          context.builder.buildFCmp(LLVM.LLVMRealONE, lhs, rhs, "fcmptmp")
        } else {
          val function = context.runtime.neqFunction
            ?: return context.report("neq function is null", descriptor)

          context.builder.buildCall(function, listOf(lhs, rhs), "neqtmp")
        }
      }
      Operation.Greater -> context.builder.buildFCmp(LLVM.LLVMRealOGT, lhs, rhs, "fcmptmp")
      Operation.GreaterEquals -> context.builder.buildFCmp(LLVM.LLVMRealOGE, lhs, rhs, "fcmptmp")
      Operation.Less -> context.builder.buildFCmp(LLVM.LLVMRealOLT, lhs, rhs, "fcmptmp")
      Operation.LessEquals -> context.builder.buildFCmp(LLVM.LLVMRealOLE, lhs, rhs, "fcmptmp")
    }
  }
}
