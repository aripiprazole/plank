package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.Builtin
import com.gabrielleeg1.plank.analyzer.PlankType
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
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value

class IfInstruction(private val descriptor: Expr.If) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val cond = !descriptor.cond.toInstruction().codegen()
    val thenStmts = { descriptor.thenBranch.map { stmt -> !stmt.toInstruction().codegen() } }
    val elseStmts = { descriptor.elseBranch.map { stmt -> !stmt.toInstruction().codegen() } }

    createIf(binding.visit(descriptor), cond, thenStmts, elseStmts)
  }

  companion object {
    fun CompilerContext.createAnd(lhs: Value, rhs: Value): CodegenResult = either {
      val variable = buildAlloca(!Builtin.Bool.toType())
      val thenStmts = { listOf(buildStore(variable, rhs)) }
      val elseStmts = { listOf(buildStore(variable, runtime.falseConstant)) }

      !createIf(Builtin.Bool, lhs, thenStmts, elseStmts)

      Right(buildLoad(variable))
    }

    fun CompilerContext.createIf(
      type: PlankType,
      cond: Value,
      thenStmts: () -> List<Value>,
      elseStmts: () -> List<Value> = ::emptyList,
    ): CodegenResult = either {
      val func = !currentFunction

      val thenBranch = context.newBasicBlock("then").also(func::addBasicBlock)
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
        func.addBasicBlock(br)
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
        ?: return Left(llvmError("insertion block is null"))

      func.addBasicBlock(mergeBranch)
      builder.positionAfter(mergeBranch)

      if (thenRet != null && elseRet != null) {
        val phiType = !type.toType()

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
}
