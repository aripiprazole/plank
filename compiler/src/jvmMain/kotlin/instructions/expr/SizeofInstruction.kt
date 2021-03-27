package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.Value

class SizeofInstruction(private val descriptor: Expr.Sizeof) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text

    val struct = context.findStructure(name)
      ?: return context.report("llvm structure is null", descriptor)

    return Value(LLVM.LLVMSizeOf(struct.ref))
  }
}
