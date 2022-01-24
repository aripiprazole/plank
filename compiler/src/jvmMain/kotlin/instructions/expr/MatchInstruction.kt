package com.gabrielleeg1.plank.compiler.instructions.expr

import com.gabrielleeg1.plank.analyzer.element.TypedMatchExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildAlloca
import com.gabrielleeg1.plank.compiler.builder.buildBr
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.builder.buildStore
import com.gabrielleeg1.plank.compiler.builder.currentFunction
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.debug
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.IRPattern
import com.gabrielleeg1.plank.compiler.instructions.expr.IfInstruction.Companion.createIf
import org.llvm4j.llvm4j.Value

class MatchInstruction(private val descriptor: TypedMatchExpr) : CompilerInstruction {
  override fun CompilerContext.codegen(): Value {
    debug {
      printf("=>> MATCH")
    }

    val targetType = descriptor.type
    val subjectType = descriptor.subject.type

    val target = buildAlloca(targetType.typegen(), "match")

    val subject = descriptor.subject.codegen()

    debug {
      printf("tag in subject %d", buildLoad(getField(subject, 0)))
      printf("subject string", buildLoad(getField(subject, 0)))
    }

    val matchBr = context.newBasicBlock("match_br")
      .also { currentFunction.addBasicBlock(it) }

    buildBr(matchBr)
    builder.positionAfter(matchBr)

    descriptor.patterns.forEach { (pattern, value) ->
      val thenStmts = {
        val instruction = value.codegen()
        val store = buildStore(target, instruction)

        listOf(instruction, store)
      }

      createIf(
        targetType,
        IRPattern.of(pattern, subject, subjectType).codegen(),
        thenStmts,
      )
    }

    debug {
      printf("<<= MATCH")
    }

    return buildLoad(target, "match.target")
  }
}
