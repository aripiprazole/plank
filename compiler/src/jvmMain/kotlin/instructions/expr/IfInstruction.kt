package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildBr
import com.lorenzoog.plank.compiler.buildCondBr
import com.lorenzoog.plank.compiler.buildICmp
import com.lorenzoog.plank.compiler.buildPhi
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.IntPredicate
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value

class IfInstruction(private val descriptor: Expr.If) : PlankInstruction() {
  override fun PlankContext.codegen(): CodegenResult = either {
    val cond = !descriptor.cond.toInstruction().codegen()
    val realCond = buildICmp(IntPredicate.Equal, cond, runtime.trueConstant, "cmp.tmp")

    val func = builder.getInsertionBlock().toNullable()
      ?.getFunction()
      ?.toNullable()
      ?: return Left("LLVM Insertion block is null")

    val thenBranch = context.newBasicBlock("then").also(func::addBasicBlock)
    var elseBranch = context.newBasicBlock("else")

    val mergeBranch = context.newBasicBlock("if.cont")

    val condBr = buildCondBr(realCond, thenBranch, elseBranch) // create condition

    val thenRet: Value?
    val elseRet: Value?

    thenBranch.also { br ->
      builder.positionAfter(br) // emit then

      val stmts = descriptor.thenBranch.map { stmt ->
        !stmt.toInstruction().codegen()
      }

      thenRet = stmts.lastOrNull()
        ?.takeIf { it.getType().getTypeKind() != TypeKind.Void }
        ?.takeIf { it.getType() != runtime.types.void }
        ?.also {
          val variable = buildAlloca(it.getType(), "then.v")

          buildStore(it, variable)
        }

      buildBr(mergeBranch)
    }

    elseBranch.also { br ->
      func.addBasicBlock(br)
      builder.positionAfter(br) // emit else

      val stmts = descriptor.elseBranch.map { stmt ->
        !stmt.toInstruction().codegen() // fixme failing on expr stmt that returns void
      }

      elseRet = stmts.lastOrNull()
        ?.takeIf { it.getType().getTypeKind() != TypeKind.Void }
        ?.takeIf { it.getType() != runtime.types.void }
        ?.also {
          val variable = buildAlloca(it.getType(), "else.v")

          buildStore(it, variable)
        }

      buildBr(mergeBranch)
    }

    elseBranch = builder.getInsertionBlock().toNullable()
      ?: return Left("LLVM Insertion block is null")

    func.addBasicBlock(mergeBranch)
    builder.positionAfter(mergeBranch)

    if (thenRet != null && elseRet != null) {
      val phiType = !binding.visit(descriptor).toType()

      return Right(
        buildPhi(phiType, "if.tmp").apply {
          addIncoming(thenBranch to thenRet)
          addIncoming(elseBranch to elseRet)
        }
      )
    }

    Right(condBr)
  }
}
