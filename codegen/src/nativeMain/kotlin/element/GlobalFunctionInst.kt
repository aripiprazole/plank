package org.plank.codegen.element

import org.plank.analyzer.PlankType
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.codegen.CodegenContext
import org.plank.codegen.alloca
import org.plank.codegen.castClosure
import org.plank.codegen.codegenError
import org.plank.codegen.mangleFunction
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Value

class GlobalFunctionInst(
  private val descriptor: ResolvedFunDecl,
  private val mangled: String,
  private val generate: GenerateBody,
  override val name: String = descriptor.name.text,
) : FunctionInst {
  override val type: PlankType = descriptor.type

  override fun CodegenContext.access(): AllocaInst? {
    return lazyLocal(name) {
      currentModule.getFunction(mangled)?.let {
        alloca(createCall(it), name)
      }
    }
  }

  override fun CodegenContext.codegen(): Value {
    val closureReturnType = descriptor.type.typegen()

    val insertionBlock = insertionBlock
    val function = FunctionType(closureReturnType).let {
      currentModule.addFunction(mangled, it)
    }

    val entry = createBasicBlock("entry")
      .also(function::appendBasicBlock)
      .also(::positionAfter)

    val closure = addCurryFunction(descriptor, false, generate)

    positionAfter(entry)

    createRet(castClosure(closure, closureReturnType))

    if (!function.verify()) {
      codegenError("Invalid function `${function.name}`")
    }

    if (insertionBlock != null) {
      positionAfter(insertionBlock)
    }

    return function
  }
}

fun CodegenContext.addGlobalFunction(descriptor: ResolvedFunDecl, generate: GenerateBody): Value {
  return addFunction(GlobalFunctionInst(descriptor, mangleFunction(descriptor), generate))
}
