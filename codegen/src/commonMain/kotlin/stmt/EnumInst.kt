package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.element.addGlobalFunction
import org.plank.codegen.instantiate
import org.plank.codegen.mangle
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Identifier

class EnumInst(private val descriptor: ResolvedEnumDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val enum = createNamedStruct(mangle(descriptor.name)) {
      elements = listOf(i8, i8.pointer())
    }

    addStruct(descriptor.name.text, descriptor.type, enum)

    descriptor.members.values.forEachIndexed { tag, (name, types, functionType) ->
      val mangled = mangle(name, descriptor.name)
      val construct = mangle(name, descriptor.name, Identifier("construct"))

      val member = createNamedStruct(mangled) {
        elements = types.typegen()
      }

      addStruct(name.text, descriptor.type, member)

      when {
        types.isEmpty() -> setSymbolLazy(name.text, descriptor.type) {
          val memberInstance = createAlloca(i8)
          val enumInstance = instantiate(enum, i8.getConstant(tag), memberInstance)

          createLoad(enumInstance)
        }
        else -> addGlobalFunction(functionType, name.text, construct) {
          val memberInstance = instantiate(member, arguments = arguments.values.toTypedArray())
          val enumInstance = instantiate(
            enum,
            i8.getConstant(tag),
            createBitCast(memberInstance, i8.pointer())
          )

          createRet(createLoad(enumInstance))
        }
      }
    }

    return i1.constantNull
  }
}
