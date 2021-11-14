package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.analyzer.visit
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
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.grammar.element.Stmt
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class FunDeclInstruction(private val descriptor: Decl.FunDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val parameters = descriptor.parameters.map { binding.visit(it) }
    val returnType = binding.visit(descriptor.returnType)
    val function = !addFunction(descriptor)

    createNestedScope(descriptor.name.text) {
      val body = context.newBasicBlock("entry").also(function::addBasicBlock)
      builder.positionAfter(body)

      function.getParameters().forEachIndexed { index, parameter ->
        val plankType = parameters.getOrNull(index)
          ?: return Left(unresolvedTypeError("type of parameter $index"))
        val type = parameter.getType()

        val entry = descriptor.realParameters.entries.toList().getOrElse(index) {
          return Left(unresolvedVariableError(parameter.getName()))
        }

        val name = entry.key.text ?: return Left(unresolvedVariableError(parameter.getName()))

        val variable = buildAlloca(type, name)

        buildStore(variable, parameter)
        addVariable(name, plankType, variable)
      }

      descriptor.body.map {
        !it.toInstruction().codegen()
      }

      if (returnType.isVoid && descriptor.body.filterIsInstance<Stmt.ReturnStmt>().isEmpty()) {
        buildReturn()
      }

      if (!function.verify()) {
        return Left(invalidFunctionError(function))
      }
    }

    Right(function)
  }
}
