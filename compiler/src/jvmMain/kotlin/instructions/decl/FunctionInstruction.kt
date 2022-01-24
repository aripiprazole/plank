package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.addGlobalFunction
import org.llvm4j.llvm4j.Value

class FunctionInstruction(private val descriptor: ResolvedFunDecl) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    return addGlobalFunction(descriptor)
  }
}
