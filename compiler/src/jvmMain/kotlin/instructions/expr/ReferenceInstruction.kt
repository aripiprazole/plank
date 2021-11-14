package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildAlloca
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either

class ReferenceInstruction(private val descriptor: Expr.Reference) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult {
    return findReference(descriptor.expr)
  }

  companion object {
    fun CompilerContext.findReference(descriptor: Expr): CodegenResult = either {
      val plankType = binding.visit(descriptor)

      Right(
        when (descriptor) {
          is Expr.Instance -> !InstanceInstruction(descriptor, isPointer = true).codegen()
          is Expr.Access -> findVariable(descriptor.name.text)
            ?: return Left(unresolvedVariableError(descriptor.name.text))
          else -> {
            val type = !plankType.toType()
            val value = !descriptor.toInstruction().codegen()

            val reference = buildAlloca(type, "ref.alloca.tmp")

            buildStore(value, value)

            reference
          }
        }
      )
    }
  }
}
