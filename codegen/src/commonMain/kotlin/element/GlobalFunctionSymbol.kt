package org.plank.codegen.element

import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.Ty
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
  override val ty: FunTy,
  override val name: String,
  private val mangled: String,
  private val references: Map<Identifier, Ty>,
  private val parameters: Map<Identifier, Ty>,
  private val returnTy: Ty,
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
    val closureReturnType = ty.typegen()

    val insertionBlock = insertionBlock
    val function = FunctionType(closureReturnType).let {
      currentModule.addFunction(mangled, it)
    }

    val entry = createBasicBlock("entry")
      .also(function::appendBasicBlock)
      .also(::positionAfter)

    val closure = addFunction(
      CurryFunctionSymbol(
        ty = ty,
        nested = false,
        references = references,
        name = name,
        mangled = mangled,
        realParameters = parameters,
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
  ty: Ty,
  name: String,
  mangled: String,
  references: Map<Identifier, Ty> = emptyMap(),
  parameters: Map<Identifier, Ty> = emptyMap(),
  returnTy: Ty,
  generate: GenerateBody,
): Value {
  return addFunction(
    GlobalFunctionSymbol(ty as FunTy, name, mangled, references, parameters, returnTy, generate)
  )
}

fun CodegenContext.addGlobalFunction(descriptor: ResolvedFunDecl, generate: GenerateBody): Value {
  return addFunction(
    GlobalFunctionSymbol(
      ty = descriptor.ty as FunTy,
      references = descriptor.references,
      name = descriptor.name.text,
      mangled = mangle(descriptor),
      parameters = descriptor.parameters,
      returnTy = descriptor.returnTy,
      generate = generate,
    )
  )
}
