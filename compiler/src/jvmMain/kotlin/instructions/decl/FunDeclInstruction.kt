package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.analyzer.visit
import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildReturn
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.invalidFunctionError
import com.lorenzoog.plank.compiler.instructions.unresolvedTypeError
import com.lorenzoog.plank.compiler.instructions.unresolvedVariableError
import com.lorenzoog.plank.compiler.verify
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

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
