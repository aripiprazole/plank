package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.analyzer.Builtin
import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class ReferenceInstruction(private val descriptor: Expr.Reference) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult {
    return findReference(descriptor.expr)
  }

  companion object {
    fun PlankContext.findReference(descriptor: Expr): CodegenResult = either {
      val plankType = binding.visit(descriptor)

      Right(
        when {
          descriptor is Expr.Access -> {
            val name = descriptor.name.text

            findVariable(name) ?: return Left("variable is null")
          }
          Builtin.Numeric.isAssignableBy(plankType) || Builtin.Bool.isAssignableBy(plankType) -> {
            val type = !plankType.toType()
            val value = !descriptor.toInstruction().codegen()

            val reference = buildAlloca(type, "ref.alloca.tmp")

            buildStore(value, reference)
          }
          else -> {
            val value = !descriptor.toInstruction().codegen()

            buildLoad(value, "reftmp")
          }
        }
      )
    }
  }
}
