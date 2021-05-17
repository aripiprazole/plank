package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.llvmError
import com.lorenzoog.plank.compiler.instructions.unresolvedFieldErrror
import com.lorenzoog.plank.compiler.instructions.unresolvedTypeError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.Constant
import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok

class InstanceInstruction(private val descriptor: Expr.Instance) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val name = descriptor.name.text

    // todo fix me finding only global types
    val struct = binding.findStruct(Expr.Access(descriptor.name, descriptor.location))
      ?: return Left(unresolvedTypeError(name))

    val llvmStruct = findStruct(name)
      ?: return Left(unresolvedTypeError(name))

    val arguments = struct.fields.map { field ->
      val (_, value) = descriptor.arguments.entries.find { it.key.text == field.name }
        ?: return Left(unresolvedFieldErrror(field.name, struct))

      !value.toInstruction().codegen()
    }

    val const = llvmStruct.getConstant(
      *arguments.map { it as Constant }.toTypedArray(),
      isPacked = false
    )

    when (const) {
      is Ok -> Right(const.value)
      is Err -> Left(llvmError(const.error.message ?: "failed to create struct instance"))
    }
  }
}
