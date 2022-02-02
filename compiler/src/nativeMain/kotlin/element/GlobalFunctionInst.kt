package com.gabrielleeg1.plank.compiler.element

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.alloca
import com.gabrielleeg1.plank.compiler.codegenError
import com.gabrielleeg1.plank.compiler.mangleFunction
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Value

class GlobalFunctionInst(
  private val descriptor: ResolvedFunDecl,
  private val mangled: String,
  private val generate: GenerateBody,
  override val name: String = descriptor.name.text,
) : FunctionInst {
  override fun CodegenContext.access(): AllocaInst? {
    return currentModule.getFunction(mangled)?.let {
      alloca(createCall(it), name)
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

    createRet(createBitCast(closure, closureReturnType))

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
