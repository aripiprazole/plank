package com.gabrielleeg1.plank.compiler.expr

import com.gabrielleeg1.plank.analyzer.element.TypedInstanceExpr
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.codegenError
import com.gabrielleeg1.plank.compiler.getInstance
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.Value

class InstanceInst(private val descriptor: TypedInstanceExpr, private val ref: Boolean = false) :
  CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    val struct = descriptor.type.typegen() as StructType // TODO: handle non-struct types

    val arguments = descriptor.type.properties
      .map { (name, property) ->
        val (_, value) = descriptor.arguments.entries.find { it.key == property.name }
          ?: codegenError("Unresolved property `${name.text}` in $struct")

        value.codegen()
      }
      .toTypedArray()

    return getInstance(struct, *arguments, ref = this@InstanceInst.ref) { index, value ->
      "$value.${descriptor.type.properties.keys.elementAt(index).text}"
    }
  }
}
