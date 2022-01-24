package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.analyzer.element.ResolvedEnumDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.IREnumConstructor
import org.llvm4j.llvm4j.Value

// enums implements tagged unions
class EnumInstruction(val descriptor: ResolvedEnumDecl) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
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
          *member.fields.map { it.typegen() }.toTypedArray(), // enum member's fields
          isPacked = false
        )
      }

      // TODO: mangle name to not clash with another type
      addStruct(mangledName, type, struct)
      addFunction(IREnumConstructor(member, descriptor))
    }

    return runtime.nullConstant
  }
}
