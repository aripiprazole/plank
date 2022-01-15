package com.gabrielleeg1.plank.compiler.instructions.decl

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.ResolvedEnumDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.IREnumConstructor

// enums implements tagged unions
class EnumInstruction(val descriptor: ResolvedEnumDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val type = descriptor.type
    val union = context.getNamedStructType(descriptor.name.text).also { enum ->
      enum.setElementTypes(
        runtime.types.tag, // type tag
        runtime.types.voidPtr, // remaining enum size
        isPacked = false
      )
    }

    addStruct(descriptor.name.text, type, union)

    descriptor.members.forEach { (name, member) ->
      val mangledName = "${descriptor.name.text}_${name.text}"
      val struct = context.getNamedStructType(mangledName).also { struct ->
        struct.setElementTypes(
          runtime.types.tag, // type tag
          *member.fields.map { it.toType().bind() }.toTypedArray(), // enum member's fields
          isPacked = false
        )
      }

      // TODO: mangle name to not clash with another type
      addStruct(mangledName, type, struct)
      addFunction(IREnumConstructor(member, descriptor)).bind()
    }

    runtime.nullConstant
  }
}
