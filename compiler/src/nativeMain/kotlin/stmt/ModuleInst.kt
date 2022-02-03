package org.plank.compiler.stmt

import org.plank.analyzer.element.ResolvedModuleDecl
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.compiler.createScopeContext
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
