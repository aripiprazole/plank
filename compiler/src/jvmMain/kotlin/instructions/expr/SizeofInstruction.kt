package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value

class SizeofInstruction(private val descriptor: Expr.Sizeof) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text ?: return context.report("name is null", descriptor)

    val llvmStructure = context.findStructure(name)
      ?: return context.report("llvm structure is null", descriptor)

    return llvmStructure.getSizeOf()
  }
}
