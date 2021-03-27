package com.lorenzoog.plank.compiler.instructions.element

import com.lorenzoog.plank.analyzer.visit
import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.grammar.element.Decl
import org.llvm4j.llvm4j.Function

class IRNamedFunction(
  override val name: String,
  override val mangledName: String,
  override val descriptor: Decl.FunDecl
) : IRFunction() {
  override fun access(context: PlankContext): Function? {
    return context.module.getFunction(mangledName).toNullable()
  }

  override fun codegen(context: PlankContext): Function? {
    val parameters = descriptor.parameters.map {
      context.map(context.binding.visit(it))
        ?: return context.report("failed to handle argument", it)
    }

    val returnType = context.map(context.binding.visit(descriptor.returnType))
      ?: return context.report("return type is null", descriptor)

    return context.llvm
      .getFunctionType(returnType, *parameters.toTypedArray(), isVariadic = false)
      .let {
        context.module.addFunction(mangledName, it)
      }
  }
}
