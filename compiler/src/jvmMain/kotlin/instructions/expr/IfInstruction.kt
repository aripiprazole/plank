package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import org.llvm4j.llvm4j.IntPredicate
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.Some

class IfInstruction(private val descriptor: Expr.If) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val cond = context.map(descriptor.cond).codegen(context)
      ?: return context.report("condition is null", descriptor)

    val realCond = context.builder
      .buildIntCompare(IntPredicate.Equal, cond, context.runtime.trueConstant, Some("cmptmp"))

    val func = context.builder.getInsertionBlock().toNullable()?.getFunction()?.toNullable()
      ?: return context.report("insertion block is null", descriptor)

    val thenBranch = context.llvm.newBasicBlock("then").also(func::addBasicBlock)
    var elseBranch = context.llvm.newBasicBlock("else")

    val mergeBranch = context.llvm.newBasicBlock("ifcont")

    val condBr = context.builder.buildConditionalBranch(
      realCond,
      thenBranch,
      elseBranch
    ) // create condition

    val thenRet: Value?
    val elseRet: Value?

    thenBranch.also { br ->
      context.builder.positionAfter(br) // emit then

      val stmts = descriptor.thenBranch.mapIndexed { index, stmt ->
        context.map(stmt).codegen(context)
          ?: return context.report("failed to handle stmt $index in then branch", descriptor)
      }

      thenRet = stmts.lastOrNull()
        ?.takeIf { it.getType().getTypeKind() != TypeKind.Void }
        ?.takeIf { it.getType() != context.runtime.types.void }
        ?.also {
          val variable = context.builder.buildAlloca(it.getType(), Some("thenv"))

          context.builder.buildStore(it, variable)
        }

      context.builder.buildBranch(mergeBranch)
    }

    elseBranch.also { br ->
      func.addBasicBlock(br)
      context.builder.positionAfter(br) // emit else

      val stmts = descriptor.elseBranch.mapIndexed { index, stmt ->
        context.map(stmt).codegen(context) // fixme failing on expr stmt that returns void
          ?: return context.report("failed to handle stmt $index in else branch", descriptor)
      }

      elseRet = stmts.lastOrNull()
        ?.takeIf { it.getType().getTypeKind() != TypeKind.Void }
        ?.takeIf { it.getType() != context.runtime.types.void }
        ?.also {
          val variable = context.builder.buildAlloca(it.getType(), Some("elsev"))
          context.builder.buildStore(it, variable)
        }

      context.builder.buildBranch(mergeBranch)
    }

    elseBranch = context.builder.getInsertionBlock().toNullable()
      ?: return context.report("insertion block is null", descriptor)

    func.addBasicBlock(mergeBranch)
    context.builder.positionAfter(mergeBranch)

    if (thenRet != null && elseRet != null) {
      val phiType = context.map(context.binding.visit(descriptor))
        ?: return context.report("phiType is null", descriptor)

      return context.builder.buildPhi(phiType, Some("iftmp")).apply {
        addIncoming(thenBranch to thenRet)
        addIncoming(elseBranch to elseRet)
      }
    }

    return condBr
  }
}
