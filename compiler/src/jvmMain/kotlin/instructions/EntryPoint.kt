package com.gabrielleeg1.plank.compiler.instructions

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildCall
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.mangleFunction

class EntryPoint : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val name = currentFile.program
      .filterIsInstance<ResolvedFunDecl>()
      .find { it.name.text == "main" }
      ?: unresolvedVariableError("main")
        .left()
        .bind<ResolvedFunDecl>()

    val main = module.getFunction(mangleFunction(name)).toNullable()

    val mainFunctionType = context.getFunctionType(
      runtime.types.int,
      runtime.types.int,
      context.getPointerType(runtime.types.string).unwrap(),
      isVariadic = false
    )

    if (main != null) {
      val mainFunction = module.addFunction("main", mainFunctionType)

      context.newBasicBlock("entry")
        .also(mainFunction::addBasicBlock)
        .also(builder::positionAfter)

      val argc = mainFunction.getParameter(0).unwrap()
      val argv = mainFunction.getParameter(1).unwrap()

      buildCall(main, listOf(argc, argv))

      buildReturn(runtime.types.int.getConstant(0))
    }

    runtime.nullConstant
  }
}
