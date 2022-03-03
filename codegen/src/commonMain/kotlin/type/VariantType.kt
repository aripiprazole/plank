package org.plank.codegen.type

import org.plank.analyzer.checker.EnumInfo
import org.plank.analyzer.checker.EnumMemberInfo
import org.plank.codegen.element.addGlobalFunction
import org.plank.codegen.getField
import org.plank.codegen.pathMangled
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.Type
import org.plank.syntax.element.toIdentifier

class VariantType(
  val enum: Type,
  val tag: Int,
  val descriptor: EnumInfo,
  val info: EnumMemberInfo,
) : CodegenType {
  override fun CodegenCtx.get(): Type {
    return findStruct(info.name.text)!!
  }

  override fun CodegenCtx.declare() {
    val struct = createNamedStruct(pathMangled { listOf(descriptor.name, info.name) }.get()) {
      elements = listOf(i8, *info.parameters.typegen().toTypedArray())
    }

    addStruct(info.name.text, struct)
  }

  override fun CodegenCtx.codegen() {
    val (_, name, _, scheme, funTy) = info

    val struct = get()
    val construct = pathMangled { listOf(name, info.name, "construct".toIdentifier()) }

    when {
      info.parameters.isEmpty() -> setSymbolLazy(name.text, scheme) {
        val instance = createMalloc(struct)
        createStore(i8.getConstant(tag, false), getField(instance, 0))
        createBitCast(instance, enum)
      }
      else -> {
        val parameters = info.parameters.withIndex().associate {
          it.index.toString().toIdentifier() to it.value
        }

        addGlobalFunction(funTy, name.text, construct, parameters = parameters) {
          var idx = 1
          val instance = createMalloc(struct)
          createStore(i8.getConstant(tag, false), getField(instance, 0))

          arguments.values.forEach { argument ->
            createStore(argument, getField(instance, idx))
            idx++
          }

          createRet(createBitCast(instance, enum))
        }
      }
    }
  }
}
