package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.element.TypedSizeofExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.getSize
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import org.llvm4j.llvm4j.Value

class SizeofInstruction(private val descriptor: TypedSizeofExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    return descriptor.type.typegen().getSize()
  }
}
