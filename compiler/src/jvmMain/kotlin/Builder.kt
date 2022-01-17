@file:Suppress("DuplicatedCode")

package com.gabrielleeg1.plank.compiler

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.compiler.instructions.llvmError
import org.bytedeco.llvm.global.LLVM
import org.bytedeco.llvm.global.LLVM.LLVMBuildStructGEP
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.BasicBlock
import org.llvm4j.llvm4j.BranchInstruction
import org.llvm4j.llvm4j.FloatPredicate
import org.llvm4j.llvm4j.FloatingPointType
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.IntPredicate
import org.llvm4j.llvm4j.IntegerType
import org.llvm4j.llvm4j.LoadInstruction
import org.llvm4j.llvm4j.PhiInstruction
import org.llvm4j.llvm4j.ReturnInstruction
import org.llvm4j.llvm4j.StoreInstruction
import org.llvm4j.llvm4j.Type
import org.llvm4j.llvm4j.Value
import org.llvm4j.llvm4j.WrapSemantics
import org.llvm4j.optional.Option

fun CompilerContext.buildReturn(value: Value? = null): ReturnInstruction {
  return builder.buildReturn(Option.of(value))
}

fun CompilerContext.buildLoad(pointer: Value, name: String? = null): LoadInstruction {
  return builder.buildLoad(pointer, Option.of(name))
}

fun CompilerContext.buildStore(pointer: Value, value: Value): StoreInstruction {
  return builder.buildStore(pointer, value)
}

fun CompilerContext.buildCall(
  function: Function,
  vararg arguments: Value,
  name: String? = null
): Value {
  return builder.buildCall(function, *arguments, name = Option.of(name))
}

fun CompilerContext.buildCall(
  function: Function,
  arguments: List<Value>,
  name: String? = null
): Value {
  return builder.buildCall(function, *arguments.toTypedArray(), name = Option.of(name))
}

fun CompilerContext.buildIAdd(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildIntAdd(lhs, rhs, WrapSemantics.Unspecified, Option.of(name))
}

fun CompilerContext.buildISub(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildIntSub(lhs, rhs, WrapSemantics.Unspecified, Option.of(name))
}

fun CompilerContext.buildIMul(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildIntMul(lhs, rhs, WrapSemantics.Unspecified, Option.of(name))
}

fun CompilerContext.buildFDiv(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildFloatDiv(lhs, rhs, Option.of(name))
}

fun CompilerContext.buildFMul(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildFloatMul(lhs, rhs, Option.of(name))
}

fun CompilerContext.buildFAdd(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildFloatAdd(lhs, rhs, Option.of(name))
}

fun CompilerContext.buildFSub(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildFloatSub(lhs, rhs, Option.of(name))
}

fun CompilerContext.buildGEP(
  aggregate: Value,
  indices: List<Value>,
  inBounds: Boolean = true,
  name: String? = null
): Value {
  return builder.buildGetElementPtr(
    aggregate,
    *indices.toTypedArray(),
    inBounds = inBounds,
    name = Option.of(name)
  )
}

fun CompilerContext.buildStructGEP(
  aggregate: Value,
  index: Int,
  name: String? = null
): Value {
  return Value(LLVMBuildStructGEP(builder.ref, aggregate.ref, index, name ?: ""))
}

fun CompilerContext.buildICmp(
  predicate: IntPredicate,
  lhs: Value,
  rhs: Value,
  name: String? = null
): Value {
  return builder.buildIntCompare(predicate, lhs, rhs, Option.of(name))
}

fun CompilerContext.buildCondBr(
  cond: Value,
  ifTrue: BasicBlock,
  ifFalse: BasicBlock
): BranchInstruction {
  return builder.buildConditionalBranch(cond, ifTrue, ifFalse)
}

fun CompilerContext.buildAlloca(type: Type, name: String? = null): AllocaInstruction {
  return builder.buildAlloca(type, Option.of(name))
}

fun CompilerContext.buildBr(label: BasicBlock): BranchInstruction {
  return builder.buildBranch(label)
}

fun CompilerContext.buildPhi(type: Type, name: String? = null): PhiInstruction {
  return builder.buildPhi(type, Option.of(name))
}

fun CompilerContext.buildFNeg(value: Value, name: String? = null): Value {
  return builder.buildFloatNeg(value, Option.of(name))
}

fun CompilerContext.buildFCmp(
  predicate: FloatPredicate,
  lhs: Value,
  rhs: Value,
  name: String? = null
): Value {
  return builder.buildFloatCompare(predicate, lhs, rhs, Option.of(name))
}

fun CompilerContext.buildGlobalStringPtr(value: String, name: String? = null): Value {
  return Value(LLVM.LLVMBuildGlobalStringPtr(builder.ref, value, name ?: ""))
}

fun CompilerContext.buildFPToUI(value: Value, type: IntegerType, name: String? = null): Value {
  return builder.buildFloatToUnsigned(value, type, Option.of(name))
}

fun CompilerContext.buildUIToFP(
  value: Value,
  type: FloatingPointType,
  name: String? = null
): Value {
  return builder.buildUnsignedToFloat(value, type, Option.of(name))
}

fun CompilerContext.buildBitcast(op: Value, type: Type, name: String? = null): Value {
  return builder.buildBitCast(op, type, Option.of(name))
}

val CompilerContext.insertionBlock: Either<CodegenViolation, BasicBlock>
  get() =
    builder.getInsertionBlock().toNullable()
      ?.right()
      ?: llvmError("can not reach function in this context").left()

val CompilerContext.currentFunction: Either<CodegenViolation, Function>
  get() =
    builder.getInsertionBlock().toNullable()
      ?.getFunction()
      ?.toNullable()
      ?.right()
      ?: llvmError("can not reach function in this context").left()
