package org.plank.codegen.element

import org.plank.analyzer.PlankType
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.codegen.CodegenContext
import org.plank.codegen.alloca
import org.plank.codegen.castClosure
import org.plank.codegen.codegenError
import org.plank.codegen.mangle
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Identifier

class GlobalFunctionSymbol(
  override val type: org.plank.analyzer.FunctionType,
  private val references: Map<Identifier, PlankType>,
  override val name: String,
  private val mangled: String,
  private val realParameters: Map<Identifier, PlankType>,
  private val generate: GenerateBody,
) : FunctionSymbol {
  override fun CodegenContext.access(): AllocaInst? {
    return lazyLocal(name) {
      currentModule.getFunction(mangled)?.let {
        alloca(createCall(it), name)
      }
    }
  }

  override fun CodegenContext.codegen(): Value {
    val closureReturnType = type.typegen()

    val insertionBlock = insertionBlock
    val function = FunctionType(closureReturnType).let {
      currentModule.addFunction(mangled, it)
    }

    val entry = createBasicBlock("entry")
      .also(function::appendBasicBlock)
      .also(::positionAfter)

    val closure = addFunction(
      CurryFunctionSymbol(
        type = type,
        nested = false,
        references = references,
        name = name,
        mangled = mangled,
        realParameters = realParameters,
        generate = generate,
      )
    )

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

fun CodegenContext.addGlobalFunction(
  type: org.plank.analyzer.FunctionType,
  name: String,
  mangled: String,
  references: Map<Identifier, PlankType> = emptyMap(),
  realParameters: Map<Identifier, PlankType> = type.realParameters,
  generate: GenerateBody,
): Value {
  return addFunction(
    GlobalFunctionSymbol(type, references, name, mangled, realParameters, generate)
  )
}

fun CodegenContext.addGlobalFunction(descriptor: ResolvedFunDecl, generate: GenerateBody): Value {
  return addFunction(
    GlobalFunctionSymbol(
      type = descriptor.ty,
      references = descriptor.references,
      name = descriptor.name.text,
      mangled = mangle(descriptor),
      realParameters = descriptor.realParameters,
      generate = generate,
    )
  )
}
