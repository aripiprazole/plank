package com.lorenzoog.jplank.compiler.instructions.expr

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.llvm.addIncoming
import com.lorenzoog.jplank.compiler.llvm.buildAlloca
import com.lorenzoog.jplank.compiler.llvm.buildBranch
import com.lorenzoog.jplank.compiler.llvm.buildCondBranch
import com.lorenzoog.jplank.compiler.llvm.buildICmp
import com.lorenzoog.jplank.compiler.llvm.buildPhi
import com.lorenzoog.jplank.compiler.llvm.buildStore
import com.lorenzoog.jplank.compiler.llvm.insertionBlock
import com.lorenzoog.jplank.compiler.llvm.setPositionAtEnd
import com.lorenzoog.jplank.element.Expr
import org.bytedeco.llvm.global.LLVM
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value

class IfInstruction(private val descriptor: Expr.If) : PlankInstruction() {
  override fun codegen(context: PlankContext): Value? {
    val cond = context.map(descriptor.cond).codegen(context)
      ?: return context.report("condition is null", descriptor)

    val realCond = context.builder
      .buildICmp(LLVM.LLVMIntEQ, cond, context.runtime.trueConstant, "cmptmp")

    val func = context.builder.insertionBlock?.getFunction()?.toNullable()
      ?: return context.report("insertion block is null", descriptor)

    val thenBranch = context.llvm.newBasicBlock("then").also(func::addBasicBlock)
    var elseBranch = context.llvm.newBasicBlock("else")

    val mergeBranch = context.llvm.newBasicBlock("ifcont")

    val condBr = context.builder.buildCondBranch(
      realCond,
      thenBranch,
      elseBranch
    ) // create condition

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
          val variable = context.builder.buildAlloca(it.getType(), "thenv")
          context.builder.buildStore(it, variable)
        }

      context.builder.buildBranch(mergeBranch)
    }

    elseBranch.also { br ->
      func.addBasicBlock(br)
      context.builder.setPositionAtEnd(br) // emit else

      val stmts = descriptor.elseBranch.mapIndexed { index, stmt ->
        context.map(stmt).codegen(context) // fixme failing on expr stmt that returns void
          ?: return context.report("failed to handle stmt $index in else branch", descriptor)
      }

      elseRet = stmts.lastOrNull()
        ?.takeIf { it.getType().getTypeKind() != TypeKind.Void }
        ?.takeIf { it.getType() != context.runtime.types.void }
        ?.also {
          val variable = context.builder.buildAlloca(it.getType(), "elsev")
          context.builder.buildStore(it, variable)
        }

      context.builder.buildBranch(mergeBranch)
    }

    elseBranch = context.builder.insertionBlock
      ?: return context.report("insertion block is null", descriptor)

    func.addBasicBlock(mergeBranch)
    context.builder.setPositionAtEnd(mergeBranch)

    if (thenRet != null && elseRet != null) {
      val phiType = context.map(context.binding.visit(descriptor))
        ?: return context.report("phiType is null", descriptor)

      return context.builder.buildPhi(phiType, "iftmp").apply {
        addIncoming(listOf(thenRet), listOf(thenBranch))
        addIncoming(listOf(elseRet), listOf(elseBranch))
      }
    }

    return condBr
  }
}
