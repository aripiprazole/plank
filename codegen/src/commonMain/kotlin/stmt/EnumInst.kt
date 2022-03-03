package org.plank.codegen.stmt

import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.element.RankedSymbol
import org.plank.codegen.element.VariantSymbol
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.type.EnumType
import org.plank.codegen.type.RankedType
import org.plank.llvm4k.ir.Value

class EnumInst(private val descriptor: ResolvedEnumDecl) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    val enum = RankedType(EnumType(descriptor), descriptor.info.scheme).also {
      addType(descriptor.name.text, it)
    }

    descriptor.members.values.forEachIndexed { tag, info ->
      val symbol = RankedSymbol(VariantSymbol(enum, tag, descriptor.info, info), info.scheme)

      setSymbol(info.name.text, symbol)
    }

    return i1.constantNull
  }
}
