package org.plank.codegen.element

import arrow.core.identity
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.infer.FunTy
import org.plank.analyzer.infer.Ty
import org.plank.codegen.CodegenContext
import org.plank.codegen.ExecContext
import org.plank.codegen.alloca
import org.plank.codegen.castClosure
import org.plank.codegen.createScopeContext
import org.plank.codegen.mangle
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Identifier

class CurryFunctionSymbol(
  override val ty: FunTy,
  override val name: String,
  private val mangled: String,
  private val nested: Boolean,
  private val references: Map<Identifier, Ty>,
  private val realParameters: Map<Identifier, Ty>,
  private val generate: GenerateBody,
) : FunctionSymbol {
  private val parameters = realParameters.entries.toList().map { it.toPair() }

  override fun CodegenContext.access(): AllocaInst? {
    return lazyLocal("curry.$name") {
      currentModule.getFunction(mangled)?.let {
        alloca(createCall(it), "curry.$name") // get instance of curried function
      }
    }
  }

  override fun CodegenContext.codegen(): Value {
    val reversedParameters = realParameters.keys
    val closure: Value

    createScopeContext(name) {
      closure = if (parameters.isNotEmpty()) {
        List(parameters.size - 1, ::identity)
          .reversed()
          .fold(generateNesting(reversedParameters.size - 1)) { acc, i ->
            generateNesting(i) { returnTy ->
              val func = acc.also { it.codegen() }.access()!!
              val ty = returnTy.typegen()

              createRet(castClosure(func, ty))
            }
          }
          .also { it.codegen() }
          .access()!!
      } else {
        addClosure(name, ty.returnTy, "${mangled}_empty", references, generate = generate)
          .also { it.codegen() }
          .access()!!
      }
    }

    if (nested) {
      setSymbol(name, ty, closure as AllocaInst)
    }

    return closure
  }

  private fun generateNesting(
    index: Int,
    builder: ExecContext.(returnType: Ty) -> Unit = { generate() }
  ): ClosureFunctionSymbol {
    val ty = FunTy(ty.nest(index), parameters[index].second)

    return ClosureFunctionSymbol(
      name = "$mangled#$index",
      mangled = "$mangled{{closure}}#$index",
      ty = ty,
      returnTy = ty.returnTy,
      references = references + parameters.dropLast(1),
      parameters = mapOf(parameters[index]),
      realParameters = realParameters,
      generate = { builder(ty.returnTy) },
    )
  }
}

fun CodegenContext.addCurryFunction(
  descriptor: ResolvedFunDecl,
  nested: Boolean = false,
  generate: GenerateBody,
): Value = addFunction(
  CurryFunctionSymbol(
    ty = descriptor.ty,
    nested = nested,
    references = descriptor.references,
    name = descriptor.name.text,
    mangled = mangle(descriptor),
    realParameters = descriptor.parameters,
    generate = generate,
  )
)
