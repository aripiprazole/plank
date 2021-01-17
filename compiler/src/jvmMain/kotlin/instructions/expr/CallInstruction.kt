package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.utils.FunctionUtils
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue

class CallInstruction(private val descriptor: Expr.Call) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val type = context.binding.visit(descriptor)

    val calleeDescriptor = context.binding.findCallee(descriptor.callee)
      ?: return context.report("could not find callee descriptor", descriptor)

    val callee = when (val callee = descriptor.callee) {
      is Expr.Access -> {
        val name = callee.name.text
          ?: return context.report("name is null", descriptor)

        val scope = context.binding.findScope(descriptor.callee)
          ?: return context.report("scope is null", descriptor.callee)

        val scopeName = if (scope.name == "Global") { // fixme
          context.currentFile.moduleName
        } else {
          scope.name
        }

        context.module.getFunction(
          FunctionUtils.generateName(name, scopeName).also {
            println("CALLING MANGLED NAME $it")
          }
        )
      }

      else -> context.map(callee).codegen(context)
    } ?: return context.report("callee is null", descriptor)

    if (callee !is FunctionValue) {
      return context.report("callee is not a function", descriptor)
    }

    val arguments = descriptor.arguments
      .mapIndexed { i, expr ->
        val realExpr = if (calleeDescriptor.parameters[i].isAny) {
          context.runtime.createObject(context, expr)
        } else {
          context.map(expr).codegen(context)
        }

        realExpr ?: return context.report("failed to handle argument", expr)
      }

    val variable = if (type.isVoid) "" else "calltmp"

    return context.builder.createCall(callee, arguments, variable)
  }
}
