package com.lorenzoog.plank.compiler

import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.BasicBlock
import org.llvm4j.llvm4j.BranchInstruction
import org.llvm4j.llvm4j.FloatPredicate
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.IntPredicate
import org.llvm4j.llvm4j.LoadInstruction
import org.llvm4j.llvm4j.PhiInstruction
import org.llvm4j.llvm4j.ReturnInstruction
import org.llvm4j.llvm4j.StoreInstruction
import org.llvm4j.llvm4j.Type
import org.llvm4j.llvm4j.Value
import org.llvm4j.llvm4j.WrapSemantics
import org.llvm4j.optional.Option

fun PlankContext.buildReturn(value: Value? = null): ReturnInstruction {
  return builder.buildReturn(Option.of(value))
}

fun PlankContext.buildLoad(pointer: Value, name: String? = null): LoadInstruction {
  return builder.buildLoad(pointer, Option.of(name))
}

fun PlankContext.buildStore(pointer: Value, value: Value): StoreInstruction {
  return builder.buildStore(pointer, value)
}

fun PlankContext.buildCall(
  function: Function,
  arguments: List<Value>,
  name: String? = null
): Value {
  return builder.buildCall(function, *arguments.toTypedArray(), name = Option.of(name))
}

fun PlankContext.buildIAdd(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildIntAdd(lhs, rhs, WrapSemantics.Unspecified, Option.of(name))
}

fun PlankContext.buildISub(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildIntSub(lhs, rhs, WrapSemantics.Unspecified, Option.of(name))
}

fun PlankContext.buildIMul(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildIntMul(lhs, rhs, WrapSemantics.Unspecified, Option.of(name))
}

fun PlankContext.buildFDiv(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildFloatDiv(lhs, rhs, Option.of(name))
}

fun PlankContext.buildFMul(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildFloatMul(lhs, rhs, Option.of(name))
}

fun PlankContext.buildFAdd(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildFloatAdd(lhs, rhs, Option.of(name))
}

fun PlankContext.buildFSub(lhs: Value, rhs: Value, name: String? = null): Value {
  return builder.buildFloatSub(lhs, rhs, Option.of(name))
}

fun PlankContext.buildGEP(
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

fun PlankContext.buildICmp(
  predicate: IntPredicate,
  lhs: Value,
  rhs: Value,
  name: String? = null
): Value {
  return builder.buildIntCompare(predicate, lhs, rhs, Option.of(name))
}

fun PlankContext.buildCondBr(
  cond: Value,
  ifTrue: BasicBlock,
  ifFalse: BasicBlock
): BranchInstruction {
  return builder.buildConditionalBranch(cond, ifTrue, ifFalse)
}

fun PlankContext.buildAlloca(type: Type, name: String? = null): AllocaInstruction {
  return builder.buildAlloca(type, Option.of(name))
}

fun PlankContext.buildBr(label: BasicBlock): BranchInstruction {
  return builder.buildBranch(label)
}

fun PlankContext.buildPhi(type: Type, name: String? = null): PhiInstruction {
  return builder.buildPhi(type, Option.of(name))
}

fun PlankContext.buildFNeg(value: Value, name: String? = null): Value {
  return builder.buildFloatNeg(value, Option.of(name))
}

fun PlankContext.buildFCmp(
  predicate: FloatPredicate,
  lhs: Value,
  rhs: Value,
  name: String? = null
): Value {
  return builder.buildFloatCompare(predicate, lhs, rhs, Option.of(name))
}
