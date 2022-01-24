package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.element.TypedAssignExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class AssignInstruction(private val descriptor: TypedAssignExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val value = descriptor.value.codegen()
    val variable = findVariable(descriptor.name.text)

    return buildStore(variable, value)
  }
}
