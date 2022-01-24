package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.element.TypedGroupExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class GroupInstruction(private val descriptor: TypedGroupExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    return descriptor.expr.codegen()
  }
}
