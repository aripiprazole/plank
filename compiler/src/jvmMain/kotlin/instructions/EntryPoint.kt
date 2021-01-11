package com.lorenzoog.jplank.compiler.instructions

import com.lorenzoog.jplank.compiler.PlankContext
import io.vexelabs.bitbuilder.llvm.ir.Value

class EntryPoint : PlankInstruction() {
  override fun codegen(context: PlankContext): Value {
    val function = context.main

    val mainFunctionType = context.llvm.getFunctionType(
      context.runtime.types.i32,
      context.runtime.types.i32,
      context.runtime.types.string.getPointerType(),
      variadic = false
    )

    val mainFunction = context.module.createFunction("main", mainFunctionType)
    mainFunction.createBlock("entry").also {
      context.builder.setPositionAtEnd(it)
    }

    if (function != null) {
      val argc = mainFunction.getParameter(0)
      val argv = mainFunction.getParameter(1)

      context.builder.createCall(function, listOf(argc, argv))
    }

    context.builder.createRet(context.runtime.types.i32.getConstant(0))

    return mainFunction
  }
}
