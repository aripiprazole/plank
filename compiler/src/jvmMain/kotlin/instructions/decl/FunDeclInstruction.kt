package com.gabrielleeg1.plank.compiler.instructions.decl

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.UnitType
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedReturnStmt
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildAlloca
import com.gabrielleeg1.plank.compiler.buildReturn
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.invalidFunctionError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import com.gabrielleeg1.plank.compiler.verify
import com.gabrielleeg1.plank.grammar.element.Identifier

class FunDeclInstruction(private val descriptor: ResolvedFunDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val parameters = descriptor.parameters
    val returnType = descriptor.returnType
    val function = addFunction(descriptor).bind()

    createNestedScope(descriptor.name.text) {
      val body = context.newBasicBlock("entry").also(function::addBasicBlock)
      builder.positionAfter(body)

      function.getParameters().forEachIndexed { index, parameter ->
        val plankType = parameters.getOrNull(index)
          ?: unresolvedTypeError("type of parameter $index")
            .left()
            .bind<PlankType>()

        val type = parameter.getType()

        val entry = descriptor.realParameters.entries.toList().getOrElse(index) {
          unresolvedVariableError(parameter.getName())
            .left()
            .bind<Map.Entry<Identifier, PlankType>>()
        }
        val name = entry.key.text

        val variable = buildAlloca(type, name)

        buildStore(variable, parameter)
        addVariable(name, plankType, variable)
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

    function
  }
}
