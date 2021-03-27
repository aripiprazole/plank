package com.lorenzoog.plank.compiler.instructions.element

import com.lorenzoog.plank.analyzer.visit
import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.CodegenError
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Either
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.Function

class IRNamedFunction(
  override val name: String,
  override val mangledName: String,
  override val descriptor: Decl.FunDecl
) : IRFunction() {
  override fun accessIn(context: PlankContext): Function? {
    return context.module.getFunction(mangledName).toNullable()
  }

  override fun PlankContext.codegen(): Either<out CodegenError, out Function> = either {
    val parameters = descriptor.parameters
      .map(binding::visit)
      .map { !it.toType() }

    val returnType = !binding.visit(descriptor.returnType).toType()

    Right(
      context.getFunctionType(returnType, *parameters.toTypedArray(), isVariadic = false).let {
        module.addFunction(mangledName, it)
      }
    )
  }
}
