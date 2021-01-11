package com.lorenzoog.jplank.compiler.instructions.decl

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Decl
import io.vexelabs.bitbuilder.llvm.ir.Value
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import org.bytedeco.llvm.global.LLVM

class ClassDeclInstruction(private val descriptor: Decl.ClassDecl) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val name = descriptor.name.text
      ?: return context.report("name is null", descriptor)

    val struct = StructType(LLVM.LLVMStructCreateNamed(context.llvm.ref, name))
    struct.setBody(
      types = descriptor.fields.map { it.type }
        .map {
          context.map(context.binding.visit(it))
            ?: return context.report("failed to handle argument", it)
        },
      packed = false
    )

    context.addStructure(name, struct)

    return null
  }
}
