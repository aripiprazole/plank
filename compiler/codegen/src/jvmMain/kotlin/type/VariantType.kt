package org.plank.codegen.type

import org.plank.analyzer.checker.EnumInfo
import org.plank.analyzer.checker.EnumMemberInfo
import org.plank.analyzer.infer.Subst
import org.plank.codegen.codegenError
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.typeMangled
import org.plank.llvm4k.ir.Type

class VariantType(
  val enum: CodegenType,
  val tag: Int,
  val descriptor: EnumInfo,
  val info: EnumMemberInfo,
) : CodegenType {
  override fun CodegenCtx.get(subst: Subst): Type {
    val name = typeMangled { listOf(descriptor.name, info.name) }.get()

    return findStruct(name)
      ?: codegenError("Unresolved enum variant $name with subst ${this.subst}")
  }

  override fun CodegenCtx.declare() {
    val name = typeMangled { listOf(descriptor.name, info.name) }.get()
    val struct = createNamedStruct(name) {
      elements = listOf(i8, *info.parameters.typegen().toTypedArray())
    }

    addStruct(name, struct)
  }

  override fun CodegenCtx.codegen() {
    return
  }

  override fun toString(): String = "VariantType(${info.name})"
}
