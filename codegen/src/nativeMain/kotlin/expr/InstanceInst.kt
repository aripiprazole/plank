package org.plank.codegen.expr

import org.plank.analyzer.element.TypedInstanceExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.codegenError
import org.plank.codegen.instantiate
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.Value

class InstanceInst(private val descriptor: TypedInstanceExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val struct = descriptor.type.typegen() as StructType // TODO: handle non-struct types

    val arguments = descriptor.type.properties
      .map { (name, property) ->
        val (_, value) = descriptor.arguments.entries.find { it.key == property.name }
          ?: codegenError("Unresolved property `${name.text}` in $struct")

        value.codegen()
      }
      .toTypedArray()

    val instance = instantiate(struct, *arguments) { index, value ->
      "$value.${descriptor.type.properties.keys.elementAt(index).text}"
    }

    return instance
  }
}
