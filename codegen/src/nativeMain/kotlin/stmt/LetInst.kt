package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedLetDecl
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.element.LazyInst
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

        return createStore(value, variable)
      }
      false -> {
        setSymbol(
          descriptor.name.text,
          LazyInst(descriptor.type, descriptor.name.text) {
            descriptor.value.codegen()
          },
        )

        i1.constantNull
      }
    }
  }
}
