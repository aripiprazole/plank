package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedUseDecl
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.codegenError
import org.plank.llvm4k.ir.Value

class ImportInst(private val descriptor: ResolvedUseDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val module = findModule(descriptor.module.name.text)
      ?: codegenError("Unresolved module `${descriptor.module.name.text}`")

    expand(module)

    return i1.constantNull
  }
}
