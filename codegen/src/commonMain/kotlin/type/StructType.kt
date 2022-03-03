package org.plank.codegen.type

import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.Type

class StructType(val descriptor: ResolvedStructDecl) : CodegenType {
  override fun CodegenCtx.get(): Type {
    return findStruct(descriptor.name.text)!!
  }

  override fun CodegenCtx.declare() {
    addStruct(
      descriptor.name.text,
      createNamedStruct(descriptor.name.text) {
        elements = descriptor.members.values.map { it.ty.typegen() }
      },
    )
  }

  override fun CodegenCtx.codegen() {
    return
  }
}
