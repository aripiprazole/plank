package com.gabrielleeg1.plank.compiler.instructions.element

import com.gabrielleeg1.plank.analyzer.visit
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.shared.Either
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either
import org.llvm4j.llvm4j.Function

class IRNamedFunction(
  override val name: String,
  override val mangledName: String,
  override val descriptor: Decl.FunDecl
) : IRFunction() {
  override fun accessIn(context: CompilerContext): Function? {
    return context.module.getFunction(mangledName).toNullable()
  }

  override fun CompilerContext.codegen(): Either<CodegenError, Function> = either {
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
