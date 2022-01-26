package com.gabrielleeg1.plank.compiler.instructions.decl

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildCall
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.addGlobalFunction
import org.llvm4j.llvm4j.Value

class NativeFunctionInstruction(private val descriptor: ResolvedFunDecl) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val externalName = descriptor.attribute("external")
      ?.takeIf { it.arguments.isNotEmpty() }
      ?.argument<String>(0)
      ?: TODO("handle mangle errors")

    val function = module.addFunction(
      externalName,
      context.getFunctionType(
        returnType = descriptor.type.actualReturnType.typegen(),
        *descriptor.realParameters.values.typegen().toTypedArray(),
        isVariadic = false,
      ),
    )

    function.getParameters().forEachIndexed { index, argument ->
      argument.setName(descriptor.realParameters.keys.elementAt(index).text)
    }

    return addGlobalFunction(descriptor) {
      val value = buildCall(function, parameters.values.toList())

      buildReturn(value)
    }
  }
}
