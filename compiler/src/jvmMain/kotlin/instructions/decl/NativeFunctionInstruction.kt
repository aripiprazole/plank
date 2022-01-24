package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildCall
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.addGlobalFunction
import com.gabrielleeg1.plank.compiler.mangleFunction
import org.llvm4j.llvm4j.Value

class NativeFunctionInstruction(private val descriptor: ResolvedFunDecl) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val function = module.addFunction(
      mangleFunction(descriptor, isNative = true),
      context.getFunctionType(
        returnType = descriptor.returnType.typegen(),
        *descriptor.realParameters.values.map { it.typegen() }.toTypedArray(),
        isVariadic = false,
      ),
    )

    return addGlobalFunction(descriptor) {
      val value = buildCall(function, parameters.values.toList())

      if (value.getType().isVoidType()) {
        buildReturn()
      } else {
        buildReturn(value)
      }
    }
  }
}
