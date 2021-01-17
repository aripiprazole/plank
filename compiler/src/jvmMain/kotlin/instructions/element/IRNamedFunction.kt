package com.lorenzoog.jplank.compiler.instructions.element

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.visit
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue

class IRNamedFunction(
  override val name: String,
  override val mangledName: String,
  override val descriptor: Decl.FunDecl
) : IRFunction() {
  override fun access(context: PlankContext): FunctionValue? {
    return context.module.getFunction(mangledName)
  }

  override fun codegen(context: PlankContext): FunctionValue? {
    val parameters = descriptor.parameters.map {
      context.map(context.binding.visit(it))
        ?: return context.report("failed to handle argument", it)
    }

    val returnType = context.map(context.binding.visit(descriptor.returnType))
      ?: return context.report("return type is null", descriptor)

    return context.llvm
      .getFunctionType(returns = returnType, *parameters.toTypedArray(), variadic = false)
      .let {
        context.module.createFunction(mangledName, it)
      }
  }
}
