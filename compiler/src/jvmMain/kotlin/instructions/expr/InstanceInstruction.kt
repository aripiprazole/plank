package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value

class InstanceInstruction(private val descriptor: Expr.Instance) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text
      ?: return context.report("name is null", descriptor)

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

    return llvmStructure.getConstant(*arguments.toTypedArray())
  }
}
