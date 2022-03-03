package org.plank.codegen.type

import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.analyzer.infer.Subst
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.typeMangled
import org.plank.llvm4k.ir.Type

class StructType(val descriptor: ResolvedStructDecl) : CodegenType {
  override fun CodegenCtx.get(subst: Subst): Type {
    return findStruct(typeMangled { listOf(descriptor.name) }.get())!!
  }

  override fun CodegenCtx.declare() {
    val name = typeMangled { listOf(descriptor.name) }.get()

    addStruct(
      name,
      createNamedStruct(name) {
        elements = descriptor.members.values.map { it.ty.typegen() }
      },
    )
  }

  override fun CodegenCtx.codegen() {
    return
  }

  override fun toString(): String = "StructType(${descriptor.name})"
}
