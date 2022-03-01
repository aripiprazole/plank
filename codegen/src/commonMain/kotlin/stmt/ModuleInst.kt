package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedModuleDecl
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.scope.CodegenContext
import org.plank.codegen.scope.createScopeContext
import org.plank.llvm4k.ir.Value

class ModuleInst(private val descriptor: ResolvedModuleDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val module = createScopeContext(descriptor.name.text) {
      descriptor.content.codegen()
    }

    addModule(module)

    return i1.constantNull
  }
}
