package org.plank.codegen.type

import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.scope.ap
import org.plank.llvm4k.ir.Type

class RankedType(val delegate: CodegenType, val scheme: Scheme, val isGeneric: Boolean) :
  CodegenType {
  private val bindings: MutableMap<Subst, CodegenType> = mutableMapOf()
  private lateinit var context: CodegenCtx

  override fun CodegenCtx.get(subst: Subst): Type {
    if (!isGeneric) return delegate.get(subst)

    bindings.getOrPut(subst) {
      context.ap(subst).run {
        delegate.apply {
          declare()
          codegen()
        }
      }
    }

    println("  - returning ${bindings[subst]}")
    println()

    return context.ap(subst).run {
      delegate.get(subst)
    }
  }

  override fun CodegenCtx.declare() {
    context = this

    if (!isGeneric) return delegate.run { codegen() }
  }

  override fun CodegenCtx.codegen() {
    context = this

    if (!isGeneric) return delegate.run { codegen() }
  }

  override fun toString(): String = "RankedType($delegate, scheme: $scheme, isGeneric: $isGeneric)"
}

fun CodegenCtx.RankedType(delegate: CodegenType, scheme: Scheme): RankedType {
  return RankedType(delegate, scheme, scheme.names.any { subst[it] == null })
}
