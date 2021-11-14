package com.gabrielleeg1.plank.compiler.instructions

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildCall
import com.gabrielleeg1.plank.compiler.buildReturn
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class EntryPoint : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val name = currentFile.program
      .filterIsInstance<Decl.FunDecl>()
      .find { it.name.text == "main" }
      ?: return Left(unresolvedVariableError("main"))

    val main = module.getFunction(mangler.mangle(this@codegen, name)).toNullable()

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

    Right(runtime.nullConstant)
  }
}
