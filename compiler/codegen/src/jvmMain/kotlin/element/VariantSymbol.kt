package org.plank.codegen.element

import org.plank.analyzer.checker.EnumInfo
import org.plank.analyzer.checker.EnumMemberInfo
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.codegen.ap
import org.plank.codegen.getField
import org.plank.codegen.pathTypeMangled
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.scope.ap
import org.plank.codegen.type.CodegenType
import org.plank.codegen.type.RankedType
import org.plank.codegen.type.VariantType
import org.plank.llvm4k.ir.User
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.toIdentifier

class VariantSymbol(
  val enum: CodegenType,
  val tag: Int,
  val descriptor: EnumInfo,
  val info: EnumMemberInfo,
) : Symbol {
  override val scheme: Scheme = info.scheme

  fun CodegenCtx.typegen(): CodegenType {
    return RankedType(VariantType(enum, tag, descriptor, info), descriptor.scheme).also {
      addType(info.name.text, it)
    }
  }

  override fun CodegenCtx.access(subst: Subst): User {
    val mangled = pathTypeMangled { listOf(descriptor.name, info.name, "variant".toIdentifier()) }

    return getSymbol(this, mangled.get(), subst)
  }

  override fun CodegenCtx.codegen(): Value {
    val (_, name, _, scheme, funTy, newSubst) = info

    ap(subst ap newSubst).apply {
      val type = typegen().apply {
        declare()
        codegen()
      }

      val enumType = enum.get(subst)
      val memberType = type.get(subst)

      val mangled = pathTypeMangled { listOf(descriptor.name, name, "variant".toIdentifier()) }

      when {
        info.parameters.isEmpty() -> setSymbolLazy(mangled.get(), scheme) {
          val instance = createMalloc(memberType)
          createStore(i8.getConstant(tag, false), getField(instance, 0))
          createBitCast(instance, enumType)
        }

        else -> {
          val parameters = info.parameters.withIndex().associate { (i, value) ->
            "_$i".toIdentifier() to value
          }

          addGlobalFunction(funTy, mangled.get(), mangled, parameters = parameters) {
            var idx = 1
            val instance = createMalloc(memberType)
            createStore(i8.getConstant(tag, false), getField(instance, 0))

            arguments.values.forEach { argument ->
              createStore(argument, getField(instance, idx))
              idx++
            }

            createRet(createBitCast(instance, enumType))
          }
        }
      }
    }

    return i1.constantNull
  }
}
