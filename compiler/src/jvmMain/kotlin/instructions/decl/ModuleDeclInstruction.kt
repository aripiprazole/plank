package com.lorenzoog.plank.compiler.instructions.decl

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class ModuleDeclInstruction(private val descriptor: Decl.ModuleDecl) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    createNestedScope(descriptor.name.text) nestedScope@{
      this@codegen.addModule(this@nestedScope)

      descriptor.content.forEach {
        !it.toInstruction().codegen()
      }
    }

    Right(runtime.nullConstant)
  }
}
