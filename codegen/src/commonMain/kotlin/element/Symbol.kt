package org.plank.codegen.element

import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.nullSubst
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.alloca
import org.plank.codegen.expr.createIf
import org.plank.codegen.getField
import org.plank.codegen.pathMangled
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.stringMangled
import org.plank.llvm4k.ir.AddrSpace
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.User
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Identifier

sealed interface Symbol : CodegenInstruction {
  val ty: Ty get() = scheme.ty
  val scheme: Scheme

  fun CodegenCtx.access(subst: Subst = nullSubst()): User?
}

class ValueSymbol(override val scheme: Scheme, private val value: User) : Symbol {
  override fun CodegenCtx.access(subst: Subst): User = value

  override fun CodegenCtx.codegen(): Value = value
}

class LazySymbol(
  override val scheme: Scheme,
  val name: String,
  val lazyValue: CodegenCtx.() -> Value,
) : Symbol {
  private var getter: Function? = null

  override fun CodegenCtx.access(subst: Subst): User? {
    val getter = getter ?: return null

    return alloca(createCall(getter, name = "lazy.call.$name"), "lazy.$name")
  }

  override fun CodegenCtx.codegen(): Value {
    val type = ty.typegen()
    val name = stringMangled { name }

    val struct = createNamedStruct(name.get()) {
      elements = listOf(type.pointer(AddrSpace.Generic))
    }

    val variable = currentModule.addGlobalVariable(name.get(), struct, AddrSpace.Generic).apply {
      initializer = struct.getConstant(
        type.pointer(AddrSpace.Generic).constPointerNull(),
        isPacked = false
      )
    }

    val insertionBlock = insertionBlock

    val mangled = pathMangled {
      listOf(Identifier(this@LazySymbol.name), Identifier("Get"))
    }

    getter = currentModule
      .addFunction(mangled.get(), FunctionType(type))
      .apply {
        positionAfter(createBasicBlock("entry").also(::appendBasicBlock))

        val field = getField(variable, 0)

        createIf(
          type, createIsNull(createLoad(field)),
          { createStore(alloca(lazyValue()), field) },
        )

        createRet(createLoad(createLoad(getField(variable, 0))))
      }

    if (insertionBlock != null) {
      positionAfter(insertionBlock)
    }

    return variable
  }
}
