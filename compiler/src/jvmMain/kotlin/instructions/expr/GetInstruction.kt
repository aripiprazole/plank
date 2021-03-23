package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.llvm.buildInBoundsGEP
import com.lorenzoog.jplank.compiler.llvm.buildLoad
import com.lorenzoog.jplank.element.Expr
import org.antlr.v4.kotlinruntime.Token
import org.llvm4j.llvm4j.Value

class GetInstruction(private val descriptor: Expr.Get) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val memberPtr = getVariable(
      context,
      descriptor,
      descriptor.receiver,
      descriptor.member
    ) ?: return context.report("variable is null", descriptor)

    val memberType = context.map(context.binding.visit(descriptor))
      ?: return context.report("member type is null", descriptor)

    return context.builder.buildLoad(memberPtr, memberType, "loadtmp")
  }

  companion object {
    fun getVariable(
      context: PlankContext,
      descriptor: Expr,
      receiverDescriptor: Expr,
      name: Token,
    ): Value? {
      val member = name.text ?: return context.report("member is null", descriptor)

      val receiver = context.map(receiverDescriptor).codegen(context)
        ?: return context.report("invalid receiver", descriptor)

      val struct = context.binding.visit(receiverDescriptor)
        as? PlankType.Struct? ?: return context.report("type is not a struct", descriptor)

      val type = context.map(struct) ?: return context.report("type is null", descriptor)

      val index = struct.fields.indexOfFirst { it.name == member }
      val indices = listOf(
        context.runtime.types.int.getConstant(0),
        context.runtime.types.int.getConstant(index),
      )

      return context.builder.buildInBoundsGEP(receiver, type, indices, "geptmp")
    }
  }
}
