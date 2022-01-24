package com.gabrielleeg1.plank.compiler.instructions.expr

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
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import org.llvm4j.llvm4j.TypeKind
import org.llvm4j.llvm4j.Value

class IfInstruction(private val descriptor: TypedIfExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    val cond = descriptor.cond.codegen()

    return createIf(
      descriptor.type,
      cond,
      thenStmts = {
        listOf(descriptor.thenBranch.codegen())
      },
      elseStmts = {
        listOf(descriptor.elseBranch!!.codegen())
      },
    )
  }

  companion object {
    fun CompilerContext.createAnd(lhs: Value, rhs: Value): Value {
      val variable = buildAlloca(BoolType.typegen())
      val thenStmts = { listOf(buildStore(variable, rhs)) }
      val elseStmts = { listOf(buildStore(variable, runtime.falseConstant)) }

      createIf(BoolType, lhs, thenStmts, elseStmts)

      return buildLoad(variable)
    }

    fun CompilerContext.createIf(
      type: PlankType,
      cond: Value,
      thenStmts: () -> List<Value>,
      elseStmts: () -> List<Value> = { emptyList<Value>() },
    ): Value {
      val currentFunction = currentFunction

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

      currentFunction.addBasicBlock(mergeBranch)
      builder.positionAfter(mergeBranch)

      if (thenRet != null && elseRet != null) {
        val phiType = type.typegen()

        return buildPhi(phiType, "if.tmp").apply {
          addIncoming(thenBranch to thenRet)
          addIncoming(elseBranch to elseRet)
        }
      }

      return condBr
    }
  }
}
