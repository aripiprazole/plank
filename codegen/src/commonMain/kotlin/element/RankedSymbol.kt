package org.plank.codegen.element

import org.plank.analyzer.infer.Subst
import org.plank.codegen.CodegenContext
import org.plank.codegen.ap
import org.plank.llvm4k.ir.User
import org.plank.llvm4k.ir.Value

class RankedSymbol(val delegate: Symbol) : Symbol by delegate {
  private val bindings: MutableMap<Subst, User> = mutableMapOf()
  private lateinit var context: CodegenContext

  override fun CodegenContext.codegen(): Value {
    context = this

    return i1.constantNull
  }

  override fun CodegenContext.access(subst: Subst): User? {
    println("Access ranked symbol $subst")
    println("  $delegate")
    println()
    return bindings.getOrPut(subst) {
      context.ap(subst).run {
        delegate.codegen()
        delegate.access()!!
      }
    }
  }
}
