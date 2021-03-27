package com.lorenzoog.plank.compiler.instructions

import com.lorenzoog.plank.compiler.PlankContext
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.None
import org.llvm4j.optional.Some

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
        .also(context.builder::positionAfter)

      val argc = mainFunction.getParameter(0).unwrap()
      val argv = mainFunction.getParameter(1).unwrap()

      context.builder.buildCall(function, argc, argv, name = None)
    }

    context.builder.buildReturn(Some(context.runtime.types.int.getConstant(0)))

    return mainFunction
  }
}
