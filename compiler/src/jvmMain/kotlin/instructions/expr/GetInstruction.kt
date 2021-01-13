package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.analyzer.getType
import com.lorenzoog.jplank.analyzer.type.PkStructure
import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.Value
import org.antlr.v4.kotlinruntime.Token

class GetInstruction(private val descriptor: Expr.Get) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val memberPtr = getVariable(
      context,
      descriptor,
      descriptor.receiver,
      descriptor.member
    ) ?: return context.report("variable is null", descriptor)

    val memberType = context.map(descriptor.getType(context.binding))
      ?: return context.report("member type is null", descriptor)

    return context.builder.createLoad(memberType, memberPtr, "loadtmp")
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
        as? PkStructure? ?: return context.report("type is not a struct", descriptor)

      val type = context.map(struct) ?: return context.report("type is null", descriptor)

      val index = struct.fields.indexOfFirst { it.name == member }
      val indices = listOf(
        context.runtime.types.int.getConstant(0),
        context.runtime.types.int.getConstant(index),
      )

      return context.builder.createGEP(receiver, type, indices, true, "geptmp")
    }
  }
}
