package org.plank.codegen.expr

import arrow.core.constant
import org.plank.analyzer.element.TypedIfExpr
import org.plank.codegen.CodegenContext
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.alloca
import org.plank.codegen.codegenError
import org.plank.codegen.createScopeContext
import org.plank.codegen.createUnit
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.Value

class IfInst(private val descriptor: TypedIfExpr) : CodegenInstruction {
  override fun CodegenContext.codegen(): Value {
    return createIf(
      descriptor.ty.typegen(),
      descriptor.cond.codegen(),
      thenStmts = { descriptor.thenBranch.codegen() },
      elseStmts = { descriptor.elseBranch?.codegen() ?: createUnit() },
    )
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
