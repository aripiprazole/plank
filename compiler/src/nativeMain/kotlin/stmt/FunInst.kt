package org.plank.compiler.stmt

import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.compiler.codegenError
import org.plank.compiler.element.BodyGenerator
import org.plank.compiler.element.addCurryFunction
import org.plank.compiler.element.addGlobalFunction
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.Value

class FunInst(private val descriptor: ResolvedFunDecl) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return when {
      descriptor.type.isNested -> addCurryFunction(descriptor, true, BodyGenerator(descriptor))
      descriptor.hasAttribute("intrinsic") -> {
        addGlobalFunction(descriptor) {
          findIntrinsic("${path.text}.${descriptor.name.text}")
            ?.build(this)
            ?: codegenError("Unable to find intrinsic `${path.text}.${descriptor.name.text}`")
        }
      }
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
