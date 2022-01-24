package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.element.TypedDerefExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class DerefInstruction(private val descriptor: TypedDerefExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    return buildLoad(descriptor.expr.codegen(), "value.tmp")
  }
}
