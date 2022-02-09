package org.plank.codegen.expr

import org.plank.analyzer.IntType
import org.plank.analyzer.element.TypedIntAddExpr
import org.plank.analyzer.element.TypedIntDivExpr
import org.plank.analyzer.element.TypedIntEQExpr
import org.plank.analyzer.element.TypedIntGTEExpr
import org.plank.analyzer.element.TypedIntGTExpr
import org.plank.analyzer.element.TypedIntLTEExpr
import org.plank.analyzer.element.TypedIntLTExpr
import org.plank.analyzer.element.TypedIntMulExpr
import org.plank.analyzer.element.TypedIntNEQExpr
import org.plank.analyzer.element.TypedIntOperationExpr
import org.plank.analyzer.element.TypedIntSubExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.llvm4k.ir.IntPredicate.EQ
import org.plank.llvm4k.ir.IntPredicate.NE
import org.plank.llvm4k.ir.IntPredicate.SGE
import org.plank.llvm4k.ir.IntPredicate.SGT
import org.plank.llvm4k.ir.IntPredicate.SLT
import org.plank.llvm4k.ir.IntPredicate.UGE
import org.plank.llvm4k.ir.IntPredicate.UGT
import org.plank.llvm4k.ir.IntPredicate.ULE
import org.plank.llvm4k.ir.IntPredicate.ULT
import org.plank.llvm4k.ir.Value

class IntOperationInst(private val descriptor: TypedIntOperationExpr) : CodegenInstruction {
  @Suppress("ComplexMethod")
  override fun CodegenContext.codegen(): Value {
    val rhs = descriptor.rhs.codegen()
    val lhs = descriptor.lhs.codegen()

    val unsigned = descriptor.type.cast<IntType>()?.unsigned ?: false

    return when (descriptor) {
      is TypedIntAddExpr -> createAdd(lhs, rhs)
      is TypedIntSubExpr -> createSub(lhs, rhs)
      is TypedIntMulExpr -> createMul(lhs, rhs)
      is TypedIntDivExpr -> if (unsigned) createUDiv(lhs, rhs) else createSDiv(lhs, rhs)
      is TypedIntEQExpr -> createICmp(EQ, lhs, rhs)
      is TypedIntNEQExpr -> createICmp(NE, lhs, rhs)
      is TypedIntGTEExpr -> createICmp(if (unsigned) UGE else SGE, lhs, rhs)
      is TypedIntGTExpr -> createICmp(if (unsigned) UGT else SGT, lhs, rhs)
      is TypedIntLTEExpr -> createICmp(if (unsigned) ULE else ULE, lhs, rhs)
      is TypedIntLTExpr -> createICmp(if (unsigned) ULT else SLT, lhs, rhs)
    }
  }
}
