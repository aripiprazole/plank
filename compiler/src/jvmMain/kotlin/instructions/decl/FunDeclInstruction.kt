package com.lorenzoog.jplank.compiler.instructions.decl

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.llvm.buildAlloca
import com.lorenzoog.jplank.compiler.llvm.buildRet
import com.lorenzoog.jplank.compiler.llvm.buildStore
import com.lorenzoog.jplank.compiler.llvm.setPositionAtEnd
import com.lorenzoog.jplank.compiler.llvm.verify
import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.Stmt
import com.lorenzoog.jplank.element.visit
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.Value

class FunDeclInstruction(private val descriptor: Decl.FunDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val returnType = context.binding.visit(descriptor.returnType)
    val function = context.addFunction(descriptor)
      ?: return context.report("failed to create function", descriptor)

    return context.createScope().let { functionContext ->
      val body = context.llvm.newBasicBlock("entry").also(function::addBasicBlock)
      functionContext.builder.setPositionAtEnd(body)

      function.getParameters().forEachIndexed { index, parameter ->
        val type = parameter.getType()

        val entry = descriptor.realParameters.entries.toList().getOrElse(index) {
          return context.report("function parameter with index $index is not defined", descriptor)
        }

        val name = entry.key.text
          ?: return context.report("parameter with index $index name is null", descriptor)

        val variable = functionContext.builder.buildAlloca(type, name)
        functionContext.builder.buildStore(parameter, variable)
        functionContext.addVariable(name, variable)
      }

      functionContext.map(descriptor.body).map {
        it.codegen(functionContext)
      }

      if (returnType.isVoid && descriptor.body.filterIsInstance<Stmt.ReturnStmt>().isEmpty()) {
        context.builder.buildRet()
      }

      if (!function.verify(LLVM.LLVMReturnStatusAction)) {
        context.report<Nothing>(function.getAsString())

        return context.report("invalid function", descriptor)
      }

      function
    }
  }
}
