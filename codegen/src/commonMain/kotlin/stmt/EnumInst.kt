package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.element.addGlobalFunction
import org.plank.codegen.getField
import org.plank.codegen.mangle
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Identifier

class EnumInst(private val descriptor: ResolvedEnumDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val enum = createNamedStruct(mangle(descriptor.name)) {
      elements = listOf(i8, i8.pointer())
    }

    addStruct(descriptor.name.text, descriptor.ty, enum)

    descriptor.members.values.forEachIndexed { tag, (name, types, functionType) ->
      val mangled = mangle(name, descriptor.name)
      val construct = mangle(name, descriptor.name, Identifier("construct"))

      val member = createNamedStruct(mangled) {
        elements = listOf(i8, *types.typegen().toTypedArray())
      }

      addStruct(name.text, descriptor.ty, member)

      when {
        types.isEmpty() -> setSymbolLazy(name.text, descriptor.ty) {
          val instance = createMalloc(member)
          createStore(i8.getConstant(tag), getField(instance, 0))
          createBitCast(instance, enum.pointer())
        }
        else -> addGlobalFunction(functionType, name.text, construct) {
          var idx = 1
          val instance = createMalloc(member)
          createStore(i8.getConstant(tag), getField(instance, 0))

          arguments.values.forEach { argument ->
            createStore(argument, getField(instance, idx))
            idx++
          }

          createRet(createBitCast(instance, enum.pointer()))
        }
      }
    }

    return i1.constantNull
  }
}
