package com.lorenzoog.jplank.compiler.instructions.decl

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Decl
import org.llvm4j.llvm4j.Value

class NativeFunDeclInstruction(private val descriptor: Decl.FunDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    return context.addFunction(descriptor)
  }
}
