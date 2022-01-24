package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.analyzer.element.ResolvedLetDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildAlloca
import com.gabrielleeg1.plank.compiler.builder.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class LetInstruction(private val descriptor: ResolvedLetDecl) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val name = descriptor.name.text

    val variable = buildAlloca(descriptor.type.typegen(), name).also {
      addVariable(name, descriptor.type, it)
    }

    val value = descriptor.value.codegen()

    return buildStore(variable, value)
  }
}
