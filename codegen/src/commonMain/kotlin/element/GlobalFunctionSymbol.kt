package org.plank.codegen.element

import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.codegen.MangledId
import org.plank.codegen.alloca
import org.plank.codegen.castClosure
import org.plank.codegen.codegenError
import org.plank.codegen.funMangled
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Identifier

class GlobalFunctionSymbol(
  override val ty: FunTy,
  override val name: String,
  override val scheme: Scheme,
  private val mangled: MangledId,
  private val references: Map<Identifier, Ty>,
  private val parameters: Map<Identifier, Ty>,
  private val generate: GenerateBody,
) : FunctionSymbol {
  override fun CodegenCtx.access(subst: Subst): AllocaInst? {
    return currentModule.getFunction(mangled.get())?.let {
      alloca(createCall(it), name)
    }
  }

  override fun CodegenCtx.codegen(): Value {
    val closureReturnType = ty.typegen()

    val insertionBlock = insertionBlock
    val function = FunctionType(closureReturnType).let {
      currentModule.addFunction(mangled.get(), it)
    }

    val entry = createBasicBlock("entry")
      .also(function::appendBasicBlock)
      .also(::positionAfter)

    val closure = addFunction(
      CurryFunctionSymbol(
        ty = ty,
        scheme = scheme,
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

fun CodegenCtx.addGlobalFunction(
  ty: FunTy,
  name: String,
  mangled: MangledId,
  references: Map<Identifier, Ty> = emptyMap(),
  parameters: Map<Identifier, Ty> = emptyMap(),
  generate: GenerateBody,
): Value {
  return addFunction(
    GlobalFunctionSymbol(ty, name, Scheme(ty), mangled, references, parameters, generate)
  )
}

fun CodegenCtx.addGlobalFunction(descriptor: ResolvedFunDecl, generate: GenerateBody): Value {
  return addFunction(
    GlobalFunctionSymbol(
      ty = descriptor.ty,
      scheme = descriptor.scheme,
      references = descriptor.references,
      name = descriptor.name.text,
      mangled = funMangled(descriptor),
      parameters = descriptor.parameters,
      generate = generate,
    ),
    descriptor.info.generics.isNotEmpty(),
  )
}
