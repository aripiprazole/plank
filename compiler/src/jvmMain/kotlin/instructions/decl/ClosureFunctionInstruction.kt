package com.gabrielleeg1.plank.compiler.instructions.decl

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.UnitType
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedReturnStmt
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildAlloca
import com.gabrielleeg1.plank.compiler.buildGlobalStringPtr
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.buildReturn
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.builder.alloca
import com.gabrielleeg1.plank.compiler.builder.getInstance
import com.gabrielleeg1.plank.compiler.insertionBlock
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.invalidFunctionError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import com.gabrielleeg1.plank.compiler.mangleFunction
import com.gabrielleeg1.plank.compiler.verify
import com.gabrielleeg1.plank.grammar.element.Identifier

class ClosureFunctionInstruction(private val descriptor: ResolvedFunDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val mangledName = mangleFunction(descriptor)
    val parameters = descriptor.parameters
    val returnType = descriptor.returnType

    val environmentType = context.getNamedStructType("Closure_${mangledName}_Environment").apply {
      setElementTypes(*references.map { it.value.toType().bind() }.toTypedArray(), isPacked = false)
    }

    val functionType = context.getFunctionType(
      descriptor.returnType.toType().bind(),
      environmentType,
      *descriptor.realParameters.values.map { it.toType().bind() }.toTypedArray(),
    )

    val closureFunctionType = context.getNamedStructType("Closure_${mangledName}_Function").apply {
      setElementTypes(
        context.getPointerType(functionType).unwrap(),
        environmentType,
        isPacked = false
      )
    }

    val function = module.addFunction(mangledName, functionType)

    val enclosingBlock = insertionBlock.bind() // All closures are nested

    createNestedScope(descriptor.name.text) {
      builder.positionAfter(context.newBasicBlock("entry").also(function::addBasicBlock))
      val environment = function.getParameter(0).unwrap() // ENVIRONMENT PARAMETER

      val instance = buildAlloca(environment.getType(), "Environment.Alloca.Tmp").also {
        buildStore(it, environment)
      }

      references.entries.forEachIndexed { index, (reference, type) ->
        val variable = buildAlloca(type.toType().bind(), reference)

        buildStore(variable, buildGlobalStringPtr("Example String", "str"))
        addVariable(reference, type, variable)
      }

      function.getParameters().drop(1).forEachIndexed { index, parameter ->
        val plankType = parameters.getOrNull(index)
          ?: unresolvedTypeError("type of parameter $index").left().bind<PlankType>()

        val type = parameter.getType()

        val (name) = descriptor.realParameters.entries.toList().getOrElse(index) {
          unresolvedVariableError(parameter.getName()).left()
            .bind<Map.Entry<Identifier, PlankType>>()
        }

        val variable = buildAlloca(type, name.text)
        buildStore(variable, parameter)
        addVariable(name.text, plankType, variable)
      }

      descriptor.content.map {
        it.toInstruction().codegen().bind()
      }

      if (returnType == UnitType && descriptor.content.filterIsInstance<ResolvedReturnStmt>()
          .isEmpty()
      ) {
        buildReturn()
      }

      ensure(function.verify()) { invalidFunctionError(function) }
    }

    builder.positionAfter(enclosingBlock)

    val variables = references.keys
      .mapNotNull { findVariableAlloca(it) }
      .map { buildLoad(it) }
      .toTypedArray()

    println(variables)

    val environment = getInstance(environmentType, *variables, name = "Nested.Environment").bind()
    val closure = getInstance(closureFunctionType, function, environment, isPointer = true).bind()

    addVariable(descriptor.name.text, descriptor.type, alloca(closure, "Closure.Pointer"))

    function
  }
}
