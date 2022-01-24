package com.gabrielleeg1.plank.compiler.instructions

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildCall
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.builder.callClosure
import com.gabrielleeg1.plank.compiler.builder.pointerType
import com.gabrielleeg1.plank.compiler.mangleFunction
import org.llvm4j.llvm4j.Value

class EntryPoint : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val name = file.program
      .filterIsInstance<ResolvedFunDecl>()
      .find { it.name.text == "main" }

    if (name != null) {
      val main = module.getFunction(mangleFunction(name)).toNullable()!!

      val mainFunctionType = context.getFunctionType(
        runtime.types.int,
        runtime.types.int,
        pointerType(runtime.types.string),
        isVariadic = false
      )

      val mainFunction = module.addFunction("main", mainFunctionType)

      context.newBasicBlock("entry")
        .also(mainFunction::addBasicBlock)
        .also(builder::positionAfter)

      val argc = mainFunction.getParameter(0).unwrap()
      val argv = mainFunction.getParameter(1).unwrap()

      callClosure(callClosure(buildCall(main), argc), argv)

      buildReturn(runtime.types.int.getConstant(0))
    }

    return runtime.nullConstant
  }
}
