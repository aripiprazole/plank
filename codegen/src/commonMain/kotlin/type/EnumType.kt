package org.plank.codegen.type

import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.codegen.idMangled
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.AddrSpace
import org.plank.llvm4k.ir.Type

class EnumType(val descriptor: ResolvedEnumDecl) : CodegenType {
  override fun CodegenCtx.get(): Type {
    return findStruct(descriptor.name.text)!!
  }

  override fun CodegenCtx.declare() {
    val struct = createNamedStruct(idMangled { descriptor.name }.get()) {
      elements = listOf(i8, i8.pointer(AddrSpace.Generic))
    }

    addStruct(descriptor.name.text, struct.pointer(AddrSpace.Generic))
  }

  override fun CodegenCtx.codegen() {
    val enum = get()

    descriptor.members.values.forEachIndexed { tag, member ->
      addType(member.name.text, VariantType(enum, tag, descriptor.info, member))
    }
  }
}
