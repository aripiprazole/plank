package org.plank.codegen

import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.codegen.expr.callClosure
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Value

class Entrypoint : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    val descriptor = file.program
      .filterIsInstance<ResolvedFunDecl>()
      .find { it.name.text == "main" }

    if (descriptor != null) {
      val function = FunctionType(
        i32,
        i32,
        i8.pointer().pointer(),
      ).let {
        currentModule.addFunction("main", it)
      }

      positionAfter(createBasicBlock("entry").also(function::appendBasicBlock))

      val main = findFunction("main")?.access() ?: codegenError("Unable to find main function")

      val (argc, argv) = function.arguments

      argc.name = "argc"
      argv.name = "argv"

      callClosure(callClosure(main, argc), argv)

      createRet(i32.getConstant(0, false))
    }

    return i1.constantNull
  }
}
