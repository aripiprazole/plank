package com.gabrielleeg1.plank.compiler.element

import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.ExecContext
import com.gabrielleeg1.plank.compiler.alloca
import com.gabrielleeg1.plank.compiler.codegenError
import com.gabrielleeg1.plank.compiler.createScopeContext
import com.gabrielleeg1.plank.compiler.getField
import com.gabrielleeg1.plank.compiler.getInstance
import com.gabrielleeg1.plank.compiler.mangleFunction
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Value

class ClosureFunctionInst(
  private val type: FunctionType,
  private val name: String,
  private val mangled: String,
  private val references: Map<Identifier, PlankType>,
  private val parameters: Map<Identifier, PlankType>,
  private val generate: GenerateBody,
  private val descriptor: ResolvedFunDecl? = null,
) : FunctionInst {
  override fun CodegenContext.access(): AllocaInst {
    return findSymbol(mangled)
  }

  override fun CodegenContext.codegen(): Value {
    val returnType = type.actualReturnType.typegen()
    val references = references.mapKeys { (name) -> name.text }

    val environmentType = createNamedStruct("closure.env.$mangled") {
      elements = references.map { it.value.typegen() }
    }

    val functionType = org.plank.llvm4k.ir.FunctionType(
      returnType,
      environmentType.pointer(),
      *parameters.values.toList().typegen().toTypedArray(),
    )

    val closureFunctionType = createNamedStruct("closure.fn.$mangled") {
      elements = listOf(functionType.pointer(), environmentType.pointer())
    }

    val function = currentModule.addFunction(mangled, functionType)

    // All closures are nested
    val enclosingBlock = insertionBlock ?: codegenError("No block in context")

    createScopeContext(name) {
      positionAfter(createBasicBlock("entry").also(function::appendBasicBlock))
      val arguments = function.arguments
      val environment = arguments.first().apply { name = "env" }

      val executionContext = ExecContext(this, returnType)

      with(executionContext) {
        references.entries.forEachIndexed { index, (reference, type) ->
          val variable = alloca(createLoad(getField(environment, index)), "env.$reference")

          if (reference in this@ClosureFunctionInst.parameters.keys.map { it.text }) {
            this.arguments[reference] = variable
          }

          setSymbol(reference, type, variable)
        }

        val realArguments = arguments.drop(1)

        realArguments.forEachIndexed(generateParameter(parameters))

        generate()
      }

      if (!function.verify()) {
        codegenError("Invalid function `${function.name}`")
      }
    }

    positionAfter(enclosingBlock)

    val variables = references.keys
      .mapNotNull { findAlloca(it) }
      .map { createLoad(it) }
      .toTypedArray()

    val environment = getInstance(environmentType, *variables, ref = true)
    val closure = getInstance(closureFunctionType, function, environment, ref = true)

    setSymbol(mangled, type, closure as AllocaInst)

    return closure
  }
}

fun CodegenContext.addIrClosure(descriptor: ResolvedFunDecl, generate: GenerateBody): Value =
  addFunction(
    ClosureFunctionInst(
      name = descriptor.name.text,
      mangled = mangleFunction(descriptor),
      type = descriptor.type,
      references = descriptor.references,
      parameters = descriptor.realParameters,
      generate = generate,
    )
  )

fun CodegenContext.addIrClosure(
  name: String,
  type: FunctionType,
  references: Map<Identifier, PlankType> = linkedMapOf(),
  generate: GenerateBody,
): ClosureFunctionInst {
  val closure = ClosureFunctionInst(
    name = name,
    mangled = name,
    type = type,
    references = references,
    parameters = type.realParameters,
    generate = generate,
  )

  addFunction(closure)

  return closure
}