package org.plank.codegen.expr

import arrow.core.constant
import org.plank.analyzer.element.TypedBlockBranch
import org.plank.analyzer.element.TypedIfBranch
import org.plank.analyzer.element.TypedIfExpr
import org.plank.analyzer.element.TypedThenBranch
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.alloca
import org.plank.codegen.codegenError
import org.plank.codegen.createScopeContext
import org.plank.codegen.createUnit
import org.plank.codegen.element.addClosure
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.Value

class IfInst(private val descriptor: TypedIfExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return createIf(
      descriptor.ty.typegen(),
      descriptor.cond.codegen(),
      thenStmts = { codegenBranch(descriptor.thenBranch) },
      elseStmts = { descriptor.elseBranch?.let(::codegenBranch) ?: createUnit() },
    )
  }
}

fun CodegenContext.codegenBranch(branch: TypedIfBranch): Value {
  return when (branch) {
    is TypedThenBranch -> branch.value.codegen()
    is TypedBlockBranch -> {
      val symbol = addClosure(
        name = "blockBranch$${branch.hashCode()}",
        returnTy = branch.ty,
        references = branch.references,
      ) {
        branch.stmts.codegen()

        createRet(branch.value.codegen())
      }

      val closure = symbol.access()
        ?: codegenError("Failed to access generated closure for block expr")

      return callClosure(closure)
    }
  }
}

fun CodegenContext.createIf(
  type: Type,
  cond: Value,
  thenStmts: CodegenContext.() -> Value,
  elseStmts: CodegenContext.() -> Value? = constant(null),
): Value {
  val insertionBlock = insertionBlock ?: codegenError("No block in context")
  val currentFunction = insertionBlock.function ?: codegenError("No function in context")

  val thenBranch = createBasicBlock("then").also(currentFunction::appendBasicBlock)
  var elseBranch = createBasicBlock("else")

  val mergeBranch = createBasicBlock("if.cont")

  val condBr = createCondBr(cond, thenBranch, elseBranch) // create condition

  val thenRet: Value?
  val elseRet: Value?

  createScopeContext(scope) {
    positionAfter(thenBranch) // emit then

    thenRet = thenStmts()
      .takeIf { it.type.kind != Type.Kind.Void }
      ?.takeIf { it.type != void }
      ?.also { alloca(it, "then.v") }

    createBr(mergeBranch)
  }

  createScopeContext(scope) {
    positionAfter(elseBranch.also(currentFunction::appendBasicBlock)) // emit else

    elseRet = elseStmts()
      ?.takeIf { it.type.kind != Type.Kind.Void }
      ?.takeIf { it.type != void }
      ?.also { alloca(it, "else.v") }

    createBr(mergeBranch)
  }

  elseBranch = this.insertionBlock!!

  currentFunction.appendBasicBlock(mergeBranch)
  positionAfter(mergeBranch)

  if (thenRet != null && elseRet != null) {
    return createPhi(type, "if.tmp").apply {
      addIncoming(thenRet, thenBranch)
      addIncoming(elseRet, elseBranch)
    }
  }

  return condBr
}
