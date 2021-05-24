package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.unresolvedVariableError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class ReferenceInstruction(private val descriptor: Expr.Reference) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult {
    return findReference(descriptor.expr)
  }

  companion object {
    fun CompilerContext.findReference(descriptor: Expr): CodegenResult = either {
      val plankType = binding.visit(descriptor)

      Right(
        when (descriptor) {
          is Expr.Access -> {
            findVariable(descriptor.name.text)
              ?: return Left(unresolvedVariableError(descriptor.name.text))
          }
          else -> {
            val type = !plankType.toType()
            val value = !descriptor.toInstruction().codegen()

            val reference = buildAlloca(type, "ref.alloca.tmp")

            buildStore(value, value) // todo fix not working with load instructions

            reference
          }
        }
      )
    }
  }
}
