package com.gabrielleeg1.plank.compiler.expr

import com.gabrielleeg1.plank.analyzer.element.TypedAccessExpr
import com.gabrielleeg1.plank.analyzer.element.TypedInstanceExpr
import com.gabrielleeg1.plank.analyzer.element.TypedRefExpr
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.alloca
import org.plank.llvm4k.ir.Value

class RefInst(private val descriptor: TypedRefExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return when (val expr = descriptor.expr) {
      is TypedInstanceExpr -> InstanceInst(expr, ref = true).codegen()
      is TypedAccessExpr -> findSymbol(expr.name.text)
      else -> alloca(expr.codegen())
    }
  }
}
