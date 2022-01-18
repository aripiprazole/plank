package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.element.TypedMatchExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildAlloca
import com.gabrielleeg1.plank.compiler.buildBr
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.currentFunction
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.IRPattern
import com.gabrielleeg1.plank.compiler.instructions.expr.IfInstruction.Companion.createIf
import org.llvm4j.llvm4j.Value

class MatchInstruction(private val descriptor: TypedMatchExpr) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    debug {
      printf("=>> MATCH")
    }

    val targetType = descriptor.type
    val subjectType = descriptor.subject.type

    val target = buildAlloca(targetType.toType().bind(), "match")

    val subject = descriptor.subject.toInstruction().codegen().bind()

    debug {
      printf("tag in subject %d", buildLoad(getField(subject, 0).bind()))
      printf("subject string", buildLoad(getField(subject, 0).bind()))
    }

    val matchBr = context.newBasicBlock("match_br")
      .also { currentFunction.bind().addBasicBlock(it) }

    buildBr(matchBr)
    builder.positionAfter(matchBr)

    descriptor.patterns.forEach { (pattern, value) ->
      val thenStmts = {
        either.eager<CodegenViolation, List<Value>> {
          val instruction = value.toInstruction().codegen().bind()
          val store = buildStore(target, instruction)

          listOf(instruction, store)
        }
      }

      createIf(
        targetType,
        IRPattern.of(pattern, subject, subjectType).codegen().bind(),
        thenStmts,
      ).bind()
    }

    debug {
      printf("<<= MATCH")
    }

    buildLoad(target, "match.target")
  }
}
