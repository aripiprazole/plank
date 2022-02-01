package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.expr.callClosure
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Value

class Entrypoint : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val descriptor = file.program
      .filterIsInstance<ResolvedFunDecl>()
      .find { it.name.text == "main" }

    if (descriptor != null) {
      val main = currentModule.getFunction(mangleFunction(descriptor))
        ?: codegenError("Unable to find main function")

      val function = FunctionType(i32, i32, i8.pointer().pointer()).let {
        currentModule.addFunction("main", it)
      }

      positionAfter(createBasicBlock("entry").also(function::appendBasicBlock))

      val (argc, argv) = function.arguments

      argc.name = "argc"
      argv.name = "argv"

      callClosure(callClosure(createCall(main), argc, name = null), argv, name = null)

      createRet(i32.getConstant(0))
    }

    return i1.constantNull
  }
}
