package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.element.Expr
import io.vexelabs.bitbuilder.llvm.ir.IntPredicate
import io.vexelabs.bitbuilder.llvm.ir.TypeKind
import io.vexelabs.bitbuilder.llvm.ir.Value

class IfInstruction(private val descriptor: Expr.If) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val cond = context.map(descriptor.cond).codegen(context)
      ?: return context.report("condition is null", descriptor)

    val realCond = context.builder
      .createICmp(cond, IntPredicate.EQ, context.runtime.trueConstant, "cmptmp")

    val func = context.builder.getInsertionBlock()?.getFunction()
      ?: return context.report("insertion block is null", descriptor)

    val thenBranch = func.createBlock("then")
    var elseBranch = context.llvm.createBasicBlock("else")

    val mergeBranch = context.llvm.createBasicBlock("ifcont")

    val condBr = context.builder.createCondBr(realCond, thenBranch, elseBranch) // create condition

    val thenRet: Value?
    val elseRet: Value?

    thenBranch.also { br ->
      context.builder.setPositionAtEnd(br) // emit then

      val stmts = descriptor.thenBranch.mapIndexed { index, stmt ->
        context.map(stmt).codegen(context)
          ?: return context.report("failed to handle stmt $index in then branch", descriptor)
      }

      thenRet = stmts.lastOrNull()
        ?.takeIf { it.getType().getTypeKind() != TypeKind.Void }
        ?.takeIf { it.getType() != context.runtime.types.void }
        ?.also {
          val variable = context.builder.createAlloca(it.getType(), "thenv")
          context.builder.createStore(it, variable)
        }

      context.builder.createBr(mergeBranch)
    }

    elseBranch.also { br ->
      func.appendBlock(br)
      context.builder.setPositionAtEnd(br) // emit else

      val stmts = descriptor.elseBranch.mapIndexed { index, stmt ->
        context.map(stmt).codegen(context) // fixme failing on expr stmt that returns void
          ?: return context.report("failed to handle stmt $index in else branch", descriptor)
      }

      elseRet = stmts.lastOrNull()
        ?.takeIf { it.getType().getTypeKind() != TypeKind.Void }
        ?.takeIf { it.getType() != context.runtime.types.void }
        ?.also {
          val variable = context.builder.createAlloca(it.getType(), "elsev")
          context.builder.createStore(it, variable)
        }

      context.builder.createBr(mergeBranch)
    }

    elseBranch = context.builder.getInsertionBlock()
      ?: return context.report("insertion block is null", descriptor)

    func.appendBlock(mergeBranch)
    context.builder.setPositionAtEnd(mergeBranch)

    if (thenRet != null && elseRet != null) {
      val phiType = context.map(context.binding.visit(descriptor))
        ?: return context.report("phiType is null", descriptor)

      val phi = context.builder
        .createPhi(phiType, "iftmp")

      phi.addIncoming(listOf(thenRet), listOf(thenBranch))
      phi.addIncoming(listOf(elseRet), listOf(elseBranch))

      return phi
    }

    return condBr
  }
}
