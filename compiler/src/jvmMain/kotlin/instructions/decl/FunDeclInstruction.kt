package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.analyzer.visit
import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildReturn
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.invalidFunctionError
import com.lorenzoog.plank.compiler.instructions.unresolvedVariableError
import com.lorenzoog.plank.compiler.verify
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.bytedeco.llvm.global.LLVM

class FunDeclInstruction(private val descriptor: Decl.FunDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val returnType = binding.visit(descriptor.returnType)
    val function = !addFunction(descriptor)

    createNestedScope(descriptor.name.text) {
      val body = context.newBasicBlock("entry").also(function::addBasicBlock)
      builder.positionAfter(body)

      function.getParameters().forEachIndexed { index, parameter ->
        val type = parameter.getType()

        val entry = descriptor.realParameters.entries.toList().getOrElse(index) {
          return Left(unresolvedVariableError(parameter.getName()))
        }

        val name = entry.key.text ?: return Left(unresolvedVariableError(parameter.getName()))

        val variable = buildAlloca(type, name)

        buildStore(parameter, variable)
        addVariable(name, variable)
      }

      descriptor.body.map {
        it.toInstruction().codegen()
      }

      if (returnType.isVoid && descriptor.body.filterIsInstance<Stmt.ReturnStmt>().isEmpty()) {
        buildReturn()
      }

      if (!function.verify(LLVM.LLVMReturnStatusAction)) {
        return Left(invalidFunctionError(function))
      }
    }

    Right(function)
  }
}
