package com.gabrielleeg1.plank.compiler.stmt

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.element.BodyGenerator
import com.gabrielleeg1.plank.compiler.element.addCurryFunction
import com.gabrielleeg1.plank.compiler.element.addGlobalFunction
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Value

class FunInst(private val descriptor: ResolvedFunDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return when {
      descriptor.type.isNested -> addCurryFunction(descriptor, true, BodyGenerator(descriptor))
      descriptor.hasAttribute("external") -> {
        val type = descriptor.type
        val realParameters = descriptor.realParameters

        val function =
          FunctionType(type.actualReturnType.typegen(), realParameters.values.typegen()).let { f ->
            val name = descriptor.attribute("external")
              ?.takeIf { it.arguments.isNotEmpty() }
              ?.argument<String>(0)
              ?: TODO("handle mangle errors")

            currentModule.addFunction(name, f)
          }

        function.arguments.forEachIndexed { index, argument ->
          argument.name = realParameters.keys.elementAt(index).text
        }

        addGlobalFunction(descriptor) {
          val value = createCall(function, arguments.values.toList())

          createRet(value)
        }
      }
      else -> addGlobalFunction(descriptor, BodyGenerator(descriptor))
    }
  }
}
