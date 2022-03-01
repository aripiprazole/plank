package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedLetDecl
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.Value

class LetInst(private val descriptor: ResolvedLetDecl) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    return when (descriptor.isNested) {
      true -> {
        val name = descriptor.name.text

        val variable = createAlloca(descriptor.ty.typegen(), name = name).also {
          setSymbol(name, descriptor.scheme, it)
        }

        val value = descriptor.value.codegen()

        createStore(value, variable)
      }
      false -> {
        setSymbolLazy(descriptor.name.text, descriptor.scheme) {
          descriptor.value.codegen()
        }

        i1.constantNull
      }
    }
  }
}
