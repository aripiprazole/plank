package org.plank.compiler.stmt

import org.plank.analyzer.element.ResolvedImportDecl
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.compiler.codegenError
import org.plank.llvm4k.ir.Value

class ImportInst(private val descriptor: ResolvedImportDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val module = findModule(descriptor.module.name.text)
      ?: codegenError("Unresolved module `${descriptor.module.name.text}`")

    expand(module)

    return i1.constantNull
  }
}
