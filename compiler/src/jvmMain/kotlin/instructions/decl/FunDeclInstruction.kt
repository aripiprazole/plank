package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.analyzer.visit
import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildReturn
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.compiler.verify
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.optional.Some

class FunDeclInstruction(private val descriptor: Decl.FunDecl) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val returnType = binding.visit(descriptor.returnType)
    val function = !addFunction(descriptor)

    createNestedScope(descriptor.name.text).let { functionContext ->
      val body = context.newBasicBlock("entry").also(function::addBasicBlock)
      functionContext.builder.positionAfter(body)

      function.getParameters().forEachIndexed { index, parameter ->
        val type = parameter.getType()

        val entry = descriptor.realParameters.entries.toList().getOrElse(index) {
          return Left("function parameter with index $index is not defined")
        }

        val name = entry.key.text
          ?: return Left("parameter with index $index name is null")

        val variable = functionContext.builder.buildAlloca(type, Some(name))
        functionContext.builder.buildStore(parameter, variable)
        functionContext.addVariable(name, variable)
      }

      functionContext.run {
        descriptor.body.map {
          it.toInstruction().codegen()
        }
      }

      if (returnType.isVoid && descriptor.body.filterIsInstance<Stmt.ReturnStmt>().isEmpty()) {
        buildReturn()
      }

      if (!function.verify(LLVM.LLVMReturnStatusAction)) {
        return Left("invalid function: ${function.getAsString()}")
      }

      Right(function)
    }
  }
}
