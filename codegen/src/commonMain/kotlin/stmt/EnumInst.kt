package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.element.addGlobalFunction
import org.plank.codegen.getField
import org.plank.codegen.mangle
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.AddrSpace
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Identifier

class EnumInst(private val descriptor: ResolvedEnumDecl) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    val enum = createNamedStruct(mangle(descriptor.name)) {
      elements = listOf(i8, i8.pointer(AddrSpace.Generic))
    }

    addStruct(descriptor.name.text, enum.pointer(AddrSpace.Generic))

    descriptor.members.values.forEachIndexed { tag, member ->
      val (_, name, _, _, funTy) = member

      val mangled = mangle(name, descriptor.name)
      val construct = mangle(name, descriptor.name, Identifier("construct"))

      val memberStruct = createNamedStruct(mangled) {
        elements = listOf(i8, *member.parameters.typegen().toTypedArray())
      }

      addStruct(name.text, memberStruct)

      when {
        member.parameters.isEmpty() -> setSymbolLazy(name.text, descriptor.ty) {
          val instance = createMalloc(memberStruct)
          createStore(i8.getConstant(tag, false), getField(instance, 0))
          createBitCast(instance, enum.pointer(AddrSpace.Generic))
        }
        else -> addGlobalFunction(
          funTy,
          name.text,
          construct,
          parameters = member.parameters
            .withIndex()
            .associate { Identifier(it.index.toString()) to it.value },
        ) {
          var idx = 1
          val instance = createMalloc(memberStruct)
          createStore(i8.getConstant(tag, false), getField(instance, 0))

          arguments.values.forEach { argument ->
            createStore(argument, getField(instance, idx))
            idx++
          }

          createRet(createBitCast(instance, enum.pointer(AddrSpace.Generic)))
        }
      }
    }

    return i1.constantNull
  }
}
