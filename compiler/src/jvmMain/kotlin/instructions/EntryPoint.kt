package com.lorenzoog.plank.compiler.instructions

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildCall
import com.lorenzoog.plank.compiler.buildReturn
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

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

    val mainFunction = module.addFunction("main", mainFunctionType)

    if (main != null) {
      context.newBasicBlock("entry")
        .also(mainFunction::addBasicBlock)
        .also(builder::positionAfter)

      val argc = mainFunction.getParameter(0).unwrap()
      val argv = mainFunction.getParameter(1).unwrap()

      buildCall(main, listOf(argc, argv))
    }

    buildReturn(runtime.types.int.getConstant(0))

    Right(runtime.nullConstant)
  }
}
