package com.gabrielleeg1.plank.compiler.instructions.element

import arrow.core.Either
import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import org.llvm4j.llvm4j.Function

class IRNamedFunction(
  override val name: String,
  override val mangledName: String,
  override val descriptor: ResolvedFunDecl,
) : IRFunction() {
  override fun accessIn(context: CompilerContext): Function? {
    return context.module.getFunction(mangledName).toNullable()
  }

  override fun CompilerContext.codegen(): Either<CodegenError, Function> = either.eager {
    module.addFunction(
      mangledName,
      context.getFunctionType(
        returnType = descriptor.returnType.toType().bind(),
        *descriptor.realParameters.values
          .map { it.toType().bind() }
          .toTypedArray(),
        isVariadic = false,
      ),
    )
  }
}
