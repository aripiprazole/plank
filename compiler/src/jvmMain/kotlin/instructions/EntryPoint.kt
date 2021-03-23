package com.lorenzoog.jplank.compiler.instructions

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.llvm.buildCall
import com.lorenzoog.jplank.compiler.llvm.buildRet
import com.lorenzoog.jplank.compiler.llvm.setPositionAtEnd
import org.llvm4j.llvm4j.Value

class EntryPoint : PlankInstruction() {
  override fun codegen(context: PlankContext): Value {
    val function = context.main

    val mainFunctionType = context.llvm.getFunctionType(
      context.runtime.types.int,
      context.runtime.types.int,
      context.llvm.getPointerType(context.runtime.types.string).unwrap(),
      isVariadic = false
    )

    val mainFunction = context.module.addFunction("main", mainFunctionType)

    if (function != null) {
      context.llvm.newBasicBlock("entry")
        .also(mainFunction::addBasicBlock)
        .also(context.builder::setPositionAtEnd)

      val argc = mainFunction.getParameter(0).unwrap()
      val argv = mainFunction.getParameter(1).unwrap()

      context.builder.buildCall(function, listOf(argc, argv))
    }

    context.builder.buildRet(context.runtime.types.int.getConstant(0))

    return mainFunction
  }
}
