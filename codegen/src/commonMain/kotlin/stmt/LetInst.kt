package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedLetDecl
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.llvm4k.ir.Value

class LetInst(private val descriptor: ResolvedLetDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return when (descriptor.isNested) {
      true -> {
        val name = descriptor.name.text

        val variable = createAlloca(descriptor.type.typegen(), name = name).also {
          setSymbol(name, descriptor.type, it)
        }

        val value = descriptor.value.codegen()

        println("$variable = $value")

        createStore(value, variable)
      }
      false -> {
        setSymbolLazy(descriptor.name.text, descriptor.type) {
          descriptor.value.codegen()
        }

        i1.constantNull
      }
    }
  }
}
