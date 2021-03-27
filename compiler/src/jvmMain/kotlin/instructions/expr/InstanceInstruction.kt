package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.Constant
import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok

class InstanceInstruction(private val descriptor: Expr.Instance) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val name = descriptor.name.text

    // todo fix me finding only global types
    val struct = binding.findStruct(Expr.Access(descriptor.name, descriptor.location))
      ?: return Left("struct is null")

    val llvmStruct = findStruct(name)
      ?: return Left("llvm struct is null")

    val arguments = struct.fields.map { field ->
      val (_, value) = descriptor.arguments.entries.find { it.key.text == field.name }
        ?: return Left("failed to find argument ${field.name}")

      !value.toInstruction().codegen()
    }

    val const = llvmStruct.getConstant(
      *arguments.map { it as Constant }.toTypedArray(),
      isPacked = false
    )

    when (const) {
      is Ok -> Right(const.value)
      is Err -> Left(const.error.message ?: "failed to create struct instance")
    }
  }
}
