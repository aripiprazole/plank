package org.plank.codegen.element

import arrow.core.identity
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.codegen.MangledId
import org.plank.codegen.alloca
import org.plank.codegen.castClosure
import org.plank.codegen.funMangled
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.scope.ExecCtx
import org.plank.codegen.scope.createScopeContext
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Identifier

class CurryFunctionSymbol(
  override val ty: FunTy,
  override val name: String,
  override val scheme: Scheme,
  private val mangled: MangledId,
  private val nested: Boolean,
  private val references: Map<Identifier, Ty>,
  private val realParameters: Map<Identifier, Ty>,
  private val generate: GenerateBody,
) : FunctionSymbol {
  private val parameters = realParameters.entries.toList().map { it.toPair() }

  override fun CodegenCtx.access(subst: Subst): AllocaInst? {
    return currentModule.getFunction(mangled.get())?.let {
      alloca(createCall(it), "curry.$name") // get instance of curried function
    }
  }

  override fun CodegenCtx.codegen(): Value {
    val reversedParameters = realParameters.keys
    val closure: Value

    createScopeContext(name) {
      closure = if (parameters.isNotEmpty()) {
        List(parameters.size - 1, ::identity)
          .reversed()
          .fold(nested(reversedParameters.size - 1)) { acc, i ->
            nested(i) { returnTy ->
              val func = acc.also { it.codegen() }.access()!!
              val ty = returnTy.typegen()

              createRet(castClosure(func, ty))
            }
          }
          .also { it.codegen() }
          .access()!!
      } else {
        addClosure(name, ty.returnTy, mangled + "_empty", references, generate = generate)
          .also { it.codegen() }
          .access()!!
      }
    }

    if (nested) {
      setSymbol(name, scheme, closure as AllocaInst)
    }

    return closure
  }

  private fun CodegenCtx.nested(idx: Int, builder: NestBuilder = { generate() }) =
    FunTy(parameters[idx].second, ty.nest(idx)).let { ty ->
      ClosureFunctionSymbol(
        name = mangled.plus("#$idx").get(),
        mangled = mangled + "{{closure}}#$idx",
        ty = ty,
        scheme = Scheme(ty),
        returnTy = ty.returnTy,
        references = references + parameters.dropLast(1),
        parameters = mapOf(parameters[idx]),
        realParameters = realParameters,
        generate = { builder(ty.returnTy) },
      )
    }
}

typealias NestBuilder = ExecCtx.(returnType: Ty) -> Unit

fun CodegenCtx.addCurryFunction(
  descriptor: ResolvedFunDecl,
  nested: Boolean = false,
  generate: GenerateBody,
): Value = addFunction(
  CurryFunctionSymbol(
    ty = descriptor.ty,
    scheme = descriptor.scheme,
    nested = nested,
    references = descriptor.references,
    name = descriptor.name.text,
    mangled = funMangled(descriptor),
    realParameters = descriptor.parameters,
    generate = generate,
  ),
)
