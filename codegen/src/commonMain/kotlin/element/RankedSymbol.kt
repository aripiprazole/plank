package org.plank.codegen.element

import org.plank.analyzer.infer.Subst
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.scope.ap
import org.plank.llvm4k.ir.User
import org.plank.llvm4k.ir.Value

class RankedSymbol(val delegate: Symbol, val isGeneric: Boolean) : Symbol by delegate {
  private val bindings: MutableMap<Subst, User> = mutableMapOf()
  private lateinit var context: CodegenCtx

  override fun CodegenCtx.codegen(): Value {
    context = this

    if (!isGeneric) return delegate.codegen()

    return i1.constantNull
  }

  override fun CodegenCtx.access(subst: Subst): User? {
    if (!isGeneric) return delegate.access(subst)

    return bindings.getOrPut(subst) {
      context.ap(subst).run {
        delegate.codegen()
        delegate.access()!!
      }
    }
  }
}
