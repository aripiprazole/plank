package org.plank.codegen.expr

import org.plank.analyzer.element.TypedEnumVariantPattern
import org.plank.analyzer.element.TypedIdentPattern
import org.plank.analyzer.element.TypedMatchExpr
import org.plank.analyzer.element.TypedPattern
import org.plank.analyzer.infer.Scheme
import org.plank.analyzer.infer.ftv
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.getField
import org.plank.codegen.scope.CodegenCtx
import org.plank.codegen.unsafeAlloca
import org.plank.llvm4k.ir.IntPredicate
import org.plank.llvm4k.ir.Value

class MatchInst(private val descriptor: TypedMatchExpr) : CodegenInstruction {
  override fun CodegenCtx.codegen(): Value {
    val type = descriptor.ty.typegen()
    val patterns = descriptor.patterns.entries.toList()

    val lastPattern = patterns.elementAt(patterns.size - 1)

    val subject = descriptor.subject.codegen()

    val value = patterns
      .reversed()
      .drop(1)
      .foldIndexed(
        fun(): Value = createIf(
          type,
          checkPattern(subject, lastPattern.key, patterns.size - 1),
          { deconstructPattern(subject, lastPattern.key); lastPattern.value.codegen() },
          { createLoad(createAlloca(type)) }
        )
      ) { index, acc, (pattern, expr) ->
        fun(): Value = createIf(
          type,
          checkPattern(subject, pattern, index),
          { deconstructPattern(subject, pattern); expr.codegen() },
          { acc.invoke() }
        )
      }
      .invoke()

    return value
  }
}

fun CodegenCtx.checkPattern(
  subject: Value,
  pattern: TypedPattern,
  index: Int, // TODO: remove
): Value {
  return when (pattern) {
    is TypedIdentPattern -> i1.getConstant(1, false) // true
    is TypedEnumVariantPattern -> {
      val tag = createLoad(getField(subject, 0))

      createICmp(IntPredicate.EQ, tag, i8.getConstant(index, false))
    }
  }
}

fun CodegenCtx.deconstructPattern(subject: Value, pattern: TypedPattern) {
  when (pattern) {
    is TypedIdentPattern -> {
      setSymbol(pattern.name.text, Scheme(pattern.ty.ftv(), pattern.ty), unsafeAlloca(subject))
    }
    is TypedEnumVariantPattern -> {
      var idx = 1
      val member = createBitCast(subject, pattern.info.ty.typegen().pointer())

      pattern.properties.forEach { nestedPattern ->
        val prop = getField(member, idx)

        deconstructPattern(prop, nestedPattern)
        idx++
      }
    }
  }
}
