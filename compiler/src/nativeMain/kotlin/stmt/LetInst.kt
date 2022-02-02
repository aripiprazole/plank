package com.gabrielleeg1.plank.compiler.stmt

import com.gabrielleeg1.plank.analyzer.element.ResolvedLetDecl
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import org.plank.llvm4k.ir.Value

class LetInst(private val descriptor: ResolvedLetDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val name = descriptor.name.text

    val variable = createAlloca(descriptor.type.typegen(), name = name).also {
      setSymbol(name, descriptor.type, it)
    }

    val value = descriptor.value.codegen()

    return createStore(value, variable)
  }
}
