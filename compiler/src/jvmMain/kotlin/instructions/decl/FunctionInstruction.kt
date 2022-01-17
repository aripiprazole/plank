package com.gabrielleeg1.plank.compiler.instructions.decl

import arrow.core.Either.Right
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
import com.gabrielleeg1.plank.compiler.insertionBlock
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.invalidFunctionError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import com.gabrielleeg1.plank.compiler.verify
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.llvm4j.llvm4j.BasicBlock
import kotlin.collections.Map.Entry

class FunctionInstruction(private val descriptor: ResolvedFunDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val parameters = descriptor.parameters
    val returnType = descriptor.returnType
    val function = addFunction(descriptor).bind()

    val enclosingBlock = insertionBlock

    createNestedScope(descriptor.name.text) {
      builder.positionAfter(context.newBasicBlock("entry").also(function::addBasicBlock))

      function.getParameters().forEachIndexed { index, parameter ->
        val plankType = parameters.getOrNull(index)
          ?: unresolvedTypeError("type of parameter $index").left().bind<PlankType>()

        val (name) = descriptor.realParameters.entries.toList().getOrElse(index) {
          unresolvedVariableError(parameter.getName()).left().bind<Entry<Identifier, PlankType>>()
        }

        val variable = buildAlloca(parameter.getType(), name.text)
        buildStore(variable, parameter)
        addVariable(name.text, plankType, variable)
      }

      descriptor.content.map {
        it.toInstruction().codegen().bind()
      }

      if (
        returnType == UnitType &&
        descriptor.content.filterIsInstance<ResolvedReturnStmt>().isEmpty()
      ) {
        buildReturn()
      }

      ensure(function.verify()) { invalidFunctionError(function) }
    }

    if (enclosingBlock is Right<BasicBlock>) {
      builder.positionAfter(enclosingBlock.value)
    }

    function
  }
}
