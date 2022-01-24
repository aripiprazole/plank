package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.element.TypedSetExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.GetInstruction.Companion.findField
import org.llvm4j.llvm4j.Value

class SetInstruction(private val descriptor: TypedSetExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val value = descriptor.value.codegen()
    val field = findField(descriptor.receiver, descriptor.member)

    buildStore(field, value)

    return field
  }
}
