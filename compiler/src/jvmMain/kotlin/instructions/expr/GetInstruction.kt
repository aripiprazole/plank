package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Identifier
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.Some

class GetInstruction(private val descriptor: Expr.Get) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val memberPtr = getVariable(
      context,
      descriptor,
      descriptor.receiver,
      descriptor.member
    ) ?: return context.report("variable is null", descriptor)

    return context.builder.buildLoad(memberPtr, Some("loadtmp"))
  }

  companion object {
    fun getVariable(
      context: PlankContext,
      descriptor: Expr,
      receiverDescriptor: Expr,
      name: Identifier,
    ): Value? {
      val member = name.text

      val receiver = context.map(receiverDescriptor).codegen(context)
        ?: return context.report("invalid receiver", descriptor)

      val struct = context.binding.visit(receiverDescriptor)
        as? PlankType.Struct? ?: return context.report("type is not a struct", descriptor)

      val index = struct.fields.indexOfFirst { it.name == member }
      val indices = listOf(
        context.runtime.types.int.getConstant(0),
        context.runtime.types.int.getConstant(index),
      )

      return context.builder
        .buildGetElementPtr(
          receiver, *indices.toTypedArray(),
          inBounds = true, name = Some("geptmp")
        )
    }
  }
}
