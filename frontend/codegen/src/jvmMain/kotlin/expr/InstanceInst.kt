package org.plank.codegen.expr

import org.plank.analyzer.element.TypedInstanceExpr
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.codegenError
import org.plank.codegen.instantiate
import org.plank.codegen.scope.CodegenCtx
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.Value

class InstanceInst(private val descriptor: TypedInstanceExpr) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    val struct = descriptor.ty.typegen() as StructType

    val arguments = descriptor.info.members
      .map { (name, property) ->
        val (_, value) = descriptor.arguments.entries.find { it.key == property.name }
          ?: codegenError("Unresolved property `${name.text}` in $struct")

        value.codegen()
      }
      .toTypedArray()

    val instance = instantiate(struct, *arguments) { index, value ->
      "$value.${descriptor.info.members.keys.elementAt(index).text}"
    }

    return createLoad(instance)
  }
}
