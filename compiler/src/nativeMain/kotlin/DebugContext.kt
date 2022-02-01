package com.gabrielleeg1.plank.compiler

import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Linkage
import org.plank.llvm4k.ir.Value

class DebugContext(private val context: CodegenContext) {
  private val printf: Function by lazy {
    FunctionType(context.void, context.i8.pointer())
      .let { context.currentModule.addFunction("printf", it) }
      .apply {
        linkage = Linkage.External
      }
  }

  fun println(message: String, vararg args: Value) {
    val debug by context.createGlobalString(message)

    context.createCall(printf, debug, *args)
  }
}
