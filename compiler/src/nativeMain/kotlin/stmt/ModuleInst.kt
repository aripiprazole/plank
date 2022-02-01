package com.gabrielleeg1.plank.compiler.stmt

import com.gabrielleeg1.plank.analyzer.element.ResolvedModuleDecl
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.createScopeContext
import org.plank.llvm4k.ir.Value

class ModuleInst(private val descriptor: ResolvedModuleDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    createScopeContext(descriptor.name.text) nested@{
      addModule(this@nested)

      descriptor.content.codegen()
    }

    return i1.constantNull
  }
}
