package com.lorenzoog.plank.compiler.instructions.element

import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildBitcast
import com.lorenzoog.plank.compiler.buildGlobalStringPtr
import com.lorenzoog.plank.compiler.buildICmp
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.builder.getField
import com.lorenzoog.plank.compiler.instructions.CodegenError
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.expr.IfInstruction.Companion.createAnd
import com.lorenzoog.plank.compiler.instructions.llvmError
import com.lorenzoog.plank.compiler.instructions.unresolvedTypeError
import com.lorenzoog.plank.compiler.instructions.unresolvedVariableError
import com.lorenzoog.plank.grammar.element.Pattern
import com.lorenzoog.plank.shared.Either
import com.lorenzoog.plank.shared.Left
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.IntPredicate
import org.llvm4j.llvm4j.PointerType
import org.llvm4j.llvm4j.Value

sealed class IRPattern : CompilerInstruction() {
  companion object {
    fun of(
      pattern: Pattern,
      subject: Value,
      type: PlankType,
      member: PlankType.Set.Member? = null
    ): IRPattern {
      return when (pattern) {
        is Pattern.Ident -> IRIdentPattern(pattern, subject, type, member)
        is Pattern.NamedTuple -> IRNamedTuplePattern(pattern, subject, type)
        else -> error("unreachable")
      }
    }
  }
}

class IRIdentPattern(
  private val pattern: Pattern.Ident,
  private val subject: Value,
  private val type: PlankType,
  private val member: PlankType.Set.Member? = null
) : IRPattern() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    type.cast<PlankType.Set>()?.let { enum ->
      if (member == null || member.fields.isNotEmpty()) {
        return@let
      }

      val (_, cmp) = !compareEnumPatterns(enum, subject, member)

      return Right(cmp)
    }

    val variable = buildAlloca(!type.toType(), "${pattern.name.text}.alloca")
    buildStore(variable, subject)

    addVariable(pattern.name.text, type, variable)

    Right(runtime.trueConstant)
  }
}

class IRNamedTuplePattern(
  private val pattern: Pattern.NamedTuple,
  private val subject: Value,
  private val type: PlankType,
) : IRPattern() {
  override fun CompilerContext.codegen(): CodegenResult = either {
    val enum = type.cast<PlankType.Set>()
      ?: return Left(llvmError("could not match named tuple without enum type"))

    val member = enum.findMember(pattern.type.text)
      ?: return Left(unresolvedVariableError(pattern.type.text))

    val (instance, cmp) = !compareEnumPatterns(enum, subject, member)

    Right(
      pattern.fields.foldIndexed(cmp) { index, acc, pattern ->
        val type = member.fields.getOrNull(index)
          ?: return Left(unresolvedVariableError("pattern $index"))

        val value = !of(pattern, buildLoad(!getField(instance, index + 1)), type, member).codegen()

        buildICmp(IntPredicate.Equal, !createAnd(acc, value), runtime.trueConstant)
      }
    )
  }
}

fun CompilerContext.compareEnumPatterns(
  enum: PlankType.Set,
  subject: Value,
  member: PlankType.Set.Member,
): Either<CodegenError, Pair<AllocaInstruction, Value>> = either {
  val mangledName = "${enum.name}_${member.name}"
  val memberType = findStruct(mangledName) ?: return Left(unresolvedTypeError(mangledName))

  val index = enum.members.indexOf(member)

  val st = buildAlloca(PointerType(subject.getType().ref).getSubtypes().first())
  buildStore(st, buildLoad(subject))

  val tag = buildLoad(!getField(st, 0), "subject.tag")
  val realTag = runtime.types.tag.getConstant(index)

  debug {
    printf("Comparing tag value of struct ${enum.name} with real tag %d:", realTag)
    printf("  instance -> %s", buildGlobalStringPtr(st.getType().getAsString()))
    printf("  field    -> ${getField(st, 0).bind().getType().getAsString()} ")
    printf("  value    -> %d", buildLoad(!getField(st, 0)))
  }

  val instance = buildAlloca(memberType, "instance.match.instance")
  val bitcast =
    buildBitcast(subject, context.getPointerType(memberType).unwrap(), "instance.match.cast")

  buildStore(instance, buildLoad(bitcast))

  Right(instance to buildICmp(IntPredicate.Equal, tag, realTag))
}
