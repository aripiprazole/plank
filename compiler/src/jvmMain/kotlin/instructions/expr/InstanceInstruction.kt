package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import org.llvm4j.llvm4j.Constant
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok

class InstanceInstruction(private val descriptor: Expr.Instance) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text

    val structure = context.binding.findStructure(Expr.Access(descriptor.name, descriptor.location))
      ?: return context.report("structure is null", descriptor)

    val llvmStructure = context.findStructure(name)
      ?: return context.report("llvm structure is null", descriptor)

    val arguments = structure.fields.mapIndexed { index, field ->
      val (_, value) = descriptor.arguments.entries.find { it.key.text == field.name }
        ?: return context.report("failed to handle argument with index $index", descriptor)

      context.map(value).codegen(context)
        ?: return context.report("failed to handle argument", value)
    }

    val const = llvmStructure
      .getConstant(*arguments.map { it as Constant }.toTypedArray(), isPacked = false)

    return when (const) {
      is Ok -> const.unwrap()
      is Err -> null
    }
  }
}
