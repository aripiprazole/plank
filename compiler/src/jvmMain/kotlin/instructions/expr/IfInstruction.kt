package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.TypedIfExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildAlloca
import com.gabrielleeg1.plank.compiler.buildBr
import com.gabrielleeg1.plank.compiler.buildCondBr
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.buildPhi
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.currentFunction
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import org.llvm4j.llvm4j.BasicBlock
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value

class IfInstruction(private val descriptor: TypedIfExpr) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val cond = descriptor.cond.toInstruction().codegen().bind()

    createIf(
      descriptor.type,
      cond,
      thenStmts = { listOf(descriptor.thenBranch.toInstruction().codegen().bind()) },
      elseStmts = { listOf(descriptor.elseBranch!!.toInstruction().codegen().bind()) },
    ).bind()
  }

  companion object {
    fun CompilerContext.createAnd(lhs: Value, rhs: Value): CodegenResult = either.eager {
      val variable = buildAlloca(PlankType.bool.toType().bind())
      val thenStmts = { listOf(buildStore(variable, rhs)) }
      val elseStmts = { listOf(buildStore(variable, runtime.falseConstant)) }

      createIf(PlankType.bool, lhs, thenStmts, elseStmts).bind()

      buildLoad(variable)
    }

    fun CompilerContext.createIf(
      type: PlankType,
      cond: Value,
      thenStmts: () -> List<Value>,
      elseStmts: () -> List<Value> = ::emptyList,
    ): CodegenResult = either.eager {
      val currentFunction = currentFunction.bind()

      val thenBranch = context.newBasicBlock("then").also(currentFunction::addBasicBlock)
      var elseBranch = context.newBasicBlock("else")

      val mergeBranch = context.newBasicBlock("if.cont")

      val condBr = buildCondBr(cond, thenBranch, elseBranch) // create condition

      val thenRet: Value?
      val elseRet: Value?

      thenBranch.also { br ->
        builder.positionAfter(br) // emit then

        thenRet = thenStmts().lastOrNull()
          ?.takeIf { it.getType().getTypeKind() != TypeKind.Void }
          ?.takeIf { it.getType() != runtime.types.void }
          ?.also {
            val variable = buildAlloca(it.getType(), "then.v")

            buildStore(it, variable)
          }

        buildBr(mergeBranch)
      }

      elseBranch.also { br ->
        currentFunction.addBasicBlock(br)
        builder.positionAfter(br) // emit else

        elseRet = elseStmts().lastOrNull()
          ?.takeIf { it.getType().getTypeKind() != TypeKind.Void }
          ?.takeIf { it.getType() != runtime.types.void }
          ?.also {
            val variable = buildAlloca(it.getType(), "else.v")

            buildStore(it, variable)
          }

        buildBr(mergeBranch)
      }

      elseBranch = builder.getInsertionBlock().toNullable()
        ?: llvmError("insertion block is null")
          .left()
          .bind<BasicBlock>()

      currentFunction.addBasicBlock(mergeBranch)
      builder.positionAfter(mergeBranch)

      if (thenRet != null && elseRet != null) {
        val phiType = type.toType().bind()

        return@eager buildPhi(phiType, "if.tmp").apply {
          addIncoming(thenBranch to thenRet)
          addIncoming(elseBranch to elseRet)
        }
      }

      condBr
    }
  }
}
