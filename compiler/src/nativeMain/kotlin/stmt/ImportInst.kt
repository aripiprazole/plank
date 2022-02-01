package com.gabrielleeg1.plank.compiler.stmt

import com.gabrielleeg1.plank.analyzer.element.ResolvedImportDecl
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.codegenError
import org.plank.llvm4k.ir.Value

class ImportInst(private val descriptor: ResolvedImportDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val module = findModule(descriptor.module.name.text)
      ?: codegenError("Unresolved module `${descriptor.module.name.text}`")

    expand(module)

    return i1.constantNull
  }
}
