package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import com.gabrielleeg1.plank.analyzer.BoolType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.TypedIfExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildAlloca
import com.gabrielleeg1.plank.compiler.builder.buildBr
import com.gabrielleeg1.plank.compiler.builder.buildCondBr
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.builder.buildPhi
import com.gabrielleeg1.plank.compiler.builder.buildStore
import com.gabrielleeg1.plank.compiler.builder.currentFunction
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import org.llvm4j.llvm4j.BasicBlock
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value

class IfInstruction(private val descriptor: TypedIfExpr) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val cond = descriptor.cond.codegen().bind()

    createIf(
      descriptor.type,
      cond,
      thenStmts = {
        either.eager {
          listOf(descriptor.thenBranch.codegen().bind())
        }
      },
      elseStmts = {
        either.eager {
          listOf(descriptor.elseBranch!!.codegen().bind())
        }
      },
    ).bind()
  }

  companion object {
    fun CompilerContext.createAnd(lhs: Value, rhs: Value): CodegenResult = either.eager {
      val variable = buildAlloca(BoolType.convertType().bind())
      val thenStmts = { listOf(buildStore(variable, rhs)).right() }
      val elseStmts = { listOf(buildStore(variable, runtime.falseConstant)).right() }

      createIf(BoolType, lhs, thenStmts, elseStmts).bind()

      buildLoad(variable)
    }

    fun CompilerContext.createIf(
      type: PlankType,
      cond: Value,
      thenStmts: () -> Either<CodegenViolation, List<Value>>,
      elseStmts: () -> Either<CodegenViolation, List<Value>> = { emptyList<Value>().right() },
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

        thenRet = thenStmts().bind().lastOrNull()
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

        elseRet = elseStmts().bind().lastOrNull()
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
        val phiType = type.convertType().bind()

        return@eager buildPhi(phiType, "if.tmp").apply {
          addIncoming(thenBranch to thenRet)
          addIncoming(elseBranch to elseRet)
        }
      }

      condBr
    }
  }
}
