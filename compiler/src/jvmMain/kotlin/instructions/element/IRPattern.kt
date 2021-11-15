package com.gabrielleeg1.plank.compiler.instructions.element

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.EnumMember
import com.gabrielleeg1.plank.analyzer.EnumType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.TypedIdentPattern
import com.gabrielleeg1.plank.analyzer.element.TypedNamedTuplePattern
import com.gabrielleeg1.plank.analyzer.element.TypedPattern
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildAlloca
import com.gabrielleeg1.plank.compiler.buildBitcast
import com.gabrielleeg1.plank.compiler.buildGlobalStringPtr
import com.gabrielleeg1.plank.compiler.buildICmp
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.IfInstruction.Companion.createAnd
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.IntPredicate
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.PointerType
import org.llvm4j.llvm4j.Value

sealed class IRPattern : CompilerInstruction() {
  companion object {
    fun of(
      pattern: TypedPattern,
      subject: Value,
      type: PlankType,
      member: EnumMember? = null
    ): IRPattern {
      return when (pattern) {
        is TypedIdentPattern -> IRIdentPattern(pattern, subject, type, member)
        is TypedNamedTuplePattern -> IRNamedTuplePattern(pattern, subject, type)
        else -> error("unreachable")
      }
    }
  }
}

class IRIdentPattern(
  private val pattern: TypedIdentPattern,
  private val subject: Value,
  private val type: PlankType,
  private val member: EnumMember? = null
) : IRPattern() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    type.cast<EnumType>()?.let cast@{ enum ->
      if (member == null || member.fields.isNotEmpty()) {
        return@cast
      }

      val (_, cmp) = compareEnumPatterns(enum, subject, member).bind()

      return@eager (cmp)
    }

    val variable = buildAlloca(type.toType().bind(), "${pattern.name.text}.alloca")
    buildStore(variable, subject)

    addVariable(pattern.name.text, type, variable)

    runtime.trueConstant
  }
}

class IRNamedTuplePattern(
  private val pattern: TypedNamedTuplePattern,
  private val subject: Value,
  private val type: PlankType,
) : IRPattern() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val enum = type.cast()
      ?: llvmError("could not match named tuple without enum type")
        .left()
        .bind<EnumType>()

    val member = enum.member(pattern.type.name)
      ?: unresolvedVariableError(pattern.type.name.text)
        .left()
        .bind<EnumMember>()

    val (instance, cmp) = compareEnumPatterns(enum, subject, member).bind()

    pattern.properties.foldIndexed(cmp) { index, acc, pattern ->
      val type = member.fields.getOrNull(index)
        ?: unresolvedVariableError("pattern $index").left().bind<PlankType>()

      val value =
        of(pattern, buildLoad(getField(instance, index + 1).bind()), type, member)
          .codegen()
          .bind()

      buildICmp(IntPredicate.Equal, createAnd(acc, value).bind(), runtime.trueConstant)
    }
  }
}

fun CompilerContext.compareEnumPatterns(
  enum: EnumType,
  subject: Value,
  member: EnumMember,
): Either<CodegenError, Pair<AllocaInstruction, Value>> = either.eager {
  val mangledName = "${enum.name}_${member.name}"
  val memberType = findStruct(mangledName)
    ?: unresolvedTypeError(mangledName)
      .left()
      .bind<NamedStructType>()

  val index = enum.tag(member.name)

  val st = buildAlloca(PointerType(subject.getType().ref).getSubtypes().first())
  buildStore(st, buildLoad(subject))

  val tag = buildLoad(getField(st, 0).bind(), "subject.tag")
  val realTag = runtime.types.tag.getConstant(index)

  debug {
    printf("Comparing tag value of struct ${enum.name} with real tag %d:", realTag)
    printf("  instance -> %s", buildGlobalStringPtr(st.getType().getAsString()))
    printf("  field    -> ${getField(st, 0).bind().getType().getAsString()} ")
    printf("  value    -> %d", buildLoad(getField(st, 0).bind()))
  }

  val instance = buildAlloca(memberType, "instance.match.instance")
  val bitcast =
    buildBitcast(subject, context.getPointerType(memberType).unwrap(), "instance.match.cast")

  buildStore(instance, buildLoad(bitcast))

  instance to buildICmp(IntPredicate.Equal, tag, realTag)
}
