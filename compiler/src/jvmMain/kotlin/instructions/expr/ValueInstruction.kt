package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class ValueInstruction(private val descriptor: Expr.Value) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val reference = !descriptor.expr.toInstruction().codegen()

    println("reference ${reference.getAsString()}")

    Right(buildLoad(reference, "value.tmp"))
  }
}
