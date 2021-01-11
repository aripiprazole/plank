package com.lorenzoog.jplank.compiler.instructions.decl

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Decl
import io.vexelabs.bitbuilder.llvm.ir.Value

class NativeFunDeclInstruction(private val descriptor: Decl.FunDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return with(FunDeclInstruction) {
      descriptor.genFunction(context)
    }
  }
}
