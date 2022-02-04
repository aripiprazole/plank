package org.plank.codegen.element

import org.plank.analyzer.PlankType
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.alloca
import org.plank.codegen.expr.createIf
import org.plank.codegen.getField
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Value

sealed interface ValueInst : CodegenInstruction {
  val type: PlankType

  fun CodegenContext.access(): AllocaInst?
}

class AllocaValue(override val type: PlankType, private val inst: AllocaInst) : ValueInst {
  override fun CodegenContext.access(): AllocaInst = inst

  override fun CodegenContext.codegen(): Value = inst
}

class LazyInst(
  override val type: PlankType,
  val name: String,
  val lazyValue: CodegenContext.() -> Value,
) : ValueInst {
  private var getter: Function? = null

  override fun CodegenContext.access(): AllocaInst? {
    return lazyLocal(name) {
      getter?.let { alloca(createCall(it), "global.lazy.$name") }
    }
  }

  override fun CodegenContext.codegen(): Value {
    val type = type.typegen()

    val struct = createNamedStruct("global.${path.text}.$name") {
      elements = listOf(type.pointer())
    }

    val variable = currentModule.addGlobalVariable(name, struct).apply {
      initializer = struct.getConstant(type.pointer().constPointerNull())
    }

    val insertionBlock = insertionBlock

    getter = currentModule
      .addFunction("_Zget.global.${path.text}.$name", FunctionType(type))
      .apply {
        positionAfter(createBasicBlock("entry").also(::appendBasicBlock))

        val field = getField(variable, 0)

        createIf(
          this@LazyInst.type,
          createIsNull(createLoad(field)),
          { listOf(createStore(alloca(lazyValue()), field)) },
        )

        createRet(createLoad(createLoad(getField(variable, 0))))
      }

    if (insertionBlock != null) {
      positionAfter(insertionBlock)
    }

    return variable
  }
}
