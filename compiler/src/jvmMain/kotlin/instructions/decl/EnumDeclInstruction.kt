package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.IREnumConstructor
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

// enums implements tagged unions
class EnumDeclInstruction(val descriptor: Decl.EnumDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val type = binding.visit(descriptor)
    val union = context.getNamedStructType(descriptor.name.text).also { enum ->
      enum.setElementTypes(
        runtime.types.tag, // type tag
        runtime.types.voidPtr, // remaining enum size
        isPacked = false
      )
    }

    addStruct(descriptor.name.text, type, union)

    descriptor.members.forEach { member ->
      val mangledName = "${descriptor.name.text}_${member.name.text}"
      val struct = context.getNamedStructType(mangledName).also { struct ->
        struct.setElementTypes(
          runtime.types.tag, // type tag
          *member.fields.map { !binding.visit(it).toType() }.toTypedArray(), // enum member's fields
          isPacked = false
        )
      }

      // TODO: mangle name to not clash with another type
      addStruct(mangledName, type, struct)
      addFunction(IREnumConstructor(member, descriptor))
    }

    Right(runtime.nullConstant)
  }
}
