package org.plank.codegen.type

import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.analyzer.infer.Subst
import org.plank.codegen.codegenError
import org.plank.codegen.element.RankedSymbol
import org.plank.codegen.element.VariantSymbol
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.typeMangled
import org.plank.llvm4k.ir.AddrSpace
import org.plank.llvm4k.ir.Type

class EnumType(val descriptor: ResolvedEnumDecl) : CodegenType {
  override fun CodegenCtx.get(subst: Subst): Type {
    val name = typeMangled { listOf(descriptor.name) }.get()

    return findStruct(name)
      ?: codegenError("Unresolved enum type $name")
  }

  override fun CodegenCtx.declare() {
    val name = typeMangled { listOf(descriptor.name) }.get()
    val struct = createNamedStruct(name) {
      elements = listOf(i8, i8.pointer(AddrSpace.Generic))
    }

    addStruct(name, struct.pointer(AddrSpace.Generic))
  }

  override fun CodegenCtx.genSubTypes(target: RankedType) {
    descriptor.members.values.forEachIndexed { i, info ->
      val variant = VariantSymbol(target, i, descriptor.info, info).apply {
        typegen()
      }
      val symbol = RankedSymbol(variant, info.scheme)

      setSymbol(info.name.text, symbol)
    }
  }

  override fun CodegenCtx.codegen() {
    return
  }

  override fun toString(): String = "EnumType(${descriptor.name})"
}
