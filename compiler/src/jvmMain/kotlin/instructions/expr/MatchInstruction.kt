package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildAlloca
import com.gabrielleeg1.plank.compiler.buildBr
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.currentFunction
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.IRPattern
import com.gabrielleeg1.plank.compiler.instructions.expr.IfInstruction.Companion.createIf
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.Right
import com.gabrielleeg1.plank.shared.either
import com.gabrielleeg1.plank.shared.map

class MatchInstruction(private val descriptor: Expr.Match) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    debug {
      printf("=>> MATCH")
    }
    val targetType = binding.findBound(descriptor)
      ?: return Left(llvmError("can not find type of match descriptor"))

    val subjectType = binding.findBound(descriptor.subject)
      ?: return Left(llvmError("can not find type of match subject"))

    val target = buildAlloca(!targetType.toType(), "match")

    val subject = !descriptor.subject.toInstruction().codegen()

    debug {
      printf("tag in subject %d", !getField(subject, 0).map(::buildLoad))
      printf("subject string", !getField(subject, 0).map(::buildLoad))
    }

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

    debug {
      printf("<<= MATCH")
    }

    Right(buildLoad(target, "match.target"))
  }
}
