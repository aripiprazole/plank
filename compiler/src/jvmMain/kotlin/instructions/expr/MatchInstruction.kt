package com.lorenzoog.plank.compiler.instructions.expr

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildBr
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.currentFunction
import com.lorenzoog.plank.compiler.insertionBlock
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.element.IRPattern
import com.lorenzoog.plank.compiler.instructions.expr.IfInstruction.Companion.createIf
import com.lorenzoog.plank.compiler.instructions.llvmError
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either

class MatchInstruction(private val descriptor: Expr.Match) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val targetType = binding.findBound(descriptor)
      ?: return Left(llvmError("can not find type of match descriptor"))

    val subjectType = binding.findBound(descriptor.subject)
      ?: return Left(llvmError("can not find type of match subject"))

    val target = buildAlloca(!targetType.toType(), "match")
    val subject = !descriptor.subject.toInstruction().codegen()

    val defaultBr = !insertionBlock

    val matchBr = context.newBasicBlock("match_br")
      .also { currentFunction.bind().addBasicBlock(it) }

    buildBr(matchBr)
    builder.positionAfter(matchBr)

    descriptor.patterns.forEach { (pattern, value) ->
      val thenStmts = {
        val instruction = !value.toInstruction().codegen()
        val store = buildStore(target, instruction)

        listOf(instruction, store)
      }

      !createIf(
        targetType,
        !IRPattern.of(pattern, subject, subjectType).codegen(),
        thenStmts,
      )
    }

    Right(buildLoad(target, "match.target"))
  }
}
