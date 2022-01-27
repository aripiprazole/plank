package com.gabrielleeg1.plank.compiler.instructions.element

import com.gabrielleeg1.plank.analyzer.EnumMember
import com.gabrielleeg1.plank.analyzer.EnumType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.TypedIdentPattern
import com.gabrielleeg1.plank.analyzer.element.TypedNamedTuplePattern
import com.gabrielleeg1.plank.analyzer.element.TypedPattern
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.buildAlloca
import com.gabrielleeg1.plank.compiler.builder.buildBitcast
import com.gabrielleeg1.plank.compiler.builder.buildGlobalStringPtr
import com.gabrielleeg1.plank.compiler.builder.buildICmp
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.builder.buildStore
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.builder.pointerType
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.expr.IfInstruction.Companion.createAnd
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.IntPredicate
import org.llvm4j.llvm4j.PointerType
import org.llvm4j.llvm4j.Value

sealed interface IRPattern : CompilerInstruction {
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
) : IRPattern {
  override fun CompilerContext.codegen(): Value {
    type.cast<EnumType>()?.let cast@{ enum ->
      if (member == null || member.fields.isNotEmpty()) {
        return@cast
      }

      val (_, cmp) = compareEnumPatterns(enum, subject, member)

      return (cmp)
    }

    val variable = buildAlloca(type.typegen(), "${pattern.name.text}.alloca")
    buildStore(variable, subject)

    addVariable(pattern.name.text, type, variable)

    return runtime.trueConstant
  }
}

class IRNamedTuplePattern(
  private val pattern: TypedNamedTuplePattern,
  private val subject: Value,
  private val type: PlankType,
) : IRPattern {
  override fun CompilerContext.codegen(): Value {
    val enum = type.cast<EnumType>() ?: llvmError("could not match named tuple without enum type")

    val member = enum.member(pattern.type.name) ?: unresolvedVariableError(pattern.type.name.text)

    val (instance, cmp) = compareEnumPatterns(enum, subject, member)

    return pattern.properties.foldIndexed(cmp) { index, acc, pattern ->
      val type = member.fields.getOrNull(index) ?: unresolvedVariableError("pattern $index")

      val value = IRPattern
        .of(pattern, buildLoad(getField(instance, index + 1)), type, member)
        .codegen()

      buildICmp(IntPredicate.Equal, createAnd(acc, value), runtime.trueConstant)
    }
  }
}

fun CompilerContext.compareEnumPatterns(
  enum: EnumType,
  subject: Value,
  member: EnumMember,
): Pair<AllocaInstruction, Value> {
  val mangledName = "${enum.name}_${member.name}"
  val memberType = findStruct(mangledName)
    ?: unresolvedTypeError(mangledName)

  val index = enum.tag(member.name)

  val st = buildAlloca(PointerType(subject.getType().ref).getSubtypes().first())
  buildStore(st, buildLoad(subject))

  val tag = buildLoad(getField(st, 0), "subject.tag")
  val realTag = runtime.types.tag.getConstant(index)

  debug {
    printf("Comparing tag value of struct ${enum.name} with real tag %d:", realTag)
    printf("  instance -> %s", buildGlobalStringPtr(st.getType().getAsString()))
    printf("  field    -> ${getField(st, 0).getType().getAsString()} ")
    printf("  value    -> %d", buildLoad(getField(st, 0)))
  }

  val instance = buildAlloca(memberType, "instance.match.instance")
  val bitcast =
    buildBitcast(subject, pointerType(memberType), "instance.match.cast")

  buildStore(instance, buildLoad(bitcast))

  return instance to buildICmp(IntPredicate.Equal, tag, realTag)
}
