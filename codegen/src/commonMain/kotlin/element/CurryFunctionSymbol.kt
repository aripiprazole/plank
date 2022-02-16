package org.plank.codegen.element

import arrow.core.identity
import org.plank.analyzer.FunctionType
import org.plank.analyzer.PlankType
import org.plank.analyzer.element.ResolvedFunDecl
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
  override val type: FunctionType,
  private val nested: Boolean,
  private val references: Map<Identifier, PlankType>,
  override val name: String,
  private val mangled: String,
  private val realParameters: Map<Identifier, PlankType>,
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
            generateNesting(i) { returnType ->
              val func = acc.also { it.codegen() }.access()!!
              val type = returnType.unsafeCast<FunctionType>().typegen()

              createRet(castClosure(func, type))
            }
          }
          .also { it.codegen() }
          .access()!!
      } else {
        addClosure(name, type, "${mangled}_empty", references, generate = generate)
          .also { it.codegen() }
          .access()!!
      }
    }

    if (nested) {
      setSymbol(name, type, closure as AllocaInst)
    }

    return closure
  }

  private fun generateNesting(
    index: Int,
    builder: ExecContext.(returnType: PlankType) -> Unit = { generate() }
  ): ClosureFunctionSymbol {
    val type = FunctionType(
      parameters[index].second,
      when (val returnType = type.nest(index)) {
        is FunctionType -> returnType.copy(isNested = true)
        else -> returnType
      }
    )

    return ClosureFunctionSymbol(
      name = "$mangled#$index",
      mangled = "$mangled{{closure}}#$index",
      type = type.copy(name = Identifier("$mangled#$index")),
      references = references + parameters,
      parameters = mapOf(parameters[index]),
      realParameters = realParameters,
      generate = { builder(type.returnType) },
    )
  }
}

fun CodegenContext.addCurryFunction(
  descriptor: ResolvedFunDecl,
  nested: Boolean = false,
  generate: GenerateBody,
): Value = addFunction(
  CurryFunctionSymbol(
    type = descriptor.ty,
    nested = nested,
    references = descriptor.references,
    name = descriptor.name.text,
    mangled = mangle(descriptor),
    realParameters = descriptor.parameters,
    generate = generate,
  )
)
