package com.lorenzoog.jplank.compiler.llvm

import org.bytedeco.llvm.global.LLVM.LLVMBuildAShr
import org.bytedeco.llvm.global.LLVM.LLVMBuildAdd
import org.bytedeco.llvm.global.LLVM.LLVMBuildAddrSpaceCast
import org.bytedeco.llvm.global.LLVM.LLVMBuildAlloca
import org.bytedeco.llvm.global.LLVM.LLVMBuildAnd
import org.bytedeco.llvm.global.LLVM.LLVMBuildBitCast
import org.bytedeco.llvm.global.LLVM.LLVMBuildBr
import org.bytedeco.llvm.global.LLVM.LLVMBuildCall
import org.bytedeco.llvm.global.LLVM.LLVMBuildCall2
import org.bytedeco.llvm.global.LLVM.LLVMBuildCondBr
import org.bytedeco.llvm.global.LLVM.LLVMBuildExactUDiv
import org.bytedeco.llvm.global.LLVM.LLVMBuildExtractElement
import org.bytedeco.llvm.global.LLVM.LLVMBuildExtractValue
import org.bytedeco.llvm.global.LLVM.LLVMBuildFAdd
import org.bytedeco.llvm.global.LLVM.LLVMBuildFCmp
import org.bytedeco.llvm.global.LLVM.LLVMBuildFDiv
import org.bytedeco.llvm.global.LLVM.LLVMBuildFMul
import org.bytedeco.llvm.global.LLVM.LLVMBuildFNeg
import org.bytedeco.llvm.global.LLVM.LLVMBuildFPToSI
import org.bytedeco.llvm.global.LLVM.LLVMBuildFPToUI
import org.bytedeco.llvm.global.LLVM.LLVMBuildFRem
import org.bytedeco.llvm.global.LLVM.LLVMBuildFSub
import org.bytedeco.llvm.global.LLVM.LLVMBuildGEP
import org.bytedeco.llvm.global.LLVM.LLVMBuildGEP2
import org.bytedeco.llvm.global.LLVM.LLVMBuildICmp
import org.bytedeco.llvm.global.LLVM.LLVMBuildInBoundsGEP
import org.bytedeco.llvm.global.LLVM.LLVMBuildInBoundsGEP2
import org.bytedeco.llvm.global.LLVM.LLVMBuildIndirectBr
import org.bytedeco.llvm.global.LLVM.LLVMBuildInsertElement
import org.bytedeco.llvm.global.LLVM.LLVMBuildInsertValue
import org.bytedeco.llvm.global.LLVM.LLVMBuildIntToPtr
import org.bytedeco.llvm.global.LLVM.LLVMBuildLShr
import org.bytedeco.llvm.global.LLVM.LLVMBuildLoad
import org.bytedeco.llvm.global.LLVM.LLVMBuildLoad2
import org.bytedeco.llvm.global.LLVM.LLVMBuildMul
import org.bytedeco.llvm.global.LLVM.LLVMBuildNSWAdd
import org.bytedeco.llvm.global.LLVM.LLVMBuildNSWMul
import org.bytedeco.llvm.global.LLVM.LLVMBuildNSWSub
import org.bytedeco.llvm.global.LLVM.LLVMBuildNUWAdd
import org.bytedeco.llvm.global.LLVM.LLVMBuildNUWMul
import org.bytedeco.llvm.global.LLVM.LLVMBuildNUWSub
import org.bytedeco.llvm.global.LLVM.LLVMBuildOr
import org.bytedeco.llvm.global.LLVM.LLVMBuildPhi
import org.bytedeco.llvm.global.LLVM.LLVMBuildPtrToInt
import org.bytedeco.llvm.global.LLVM.LLVMBuildRet
import org.bytedeco.llvm.global.LLVM.LLVMBuildRetVoid
import org.bytedeco.llvm.global.LLVM.LLVMBuildSDiv
import org.bytedeco.llvm.global.LLVM.LLVMBuildSIToFP
import org.bytedeco.llvm.global.LLVM.LLVMBuildSRem
import org.bytedeco.llvm.global.LLVM.LLVMBuildSelect
import org.bytedeco.llvm.global.LLVM.LLVMBuildShl
import org.bytedeco.llvm.global.LLVM.LLVMBuildStore
import org.bytedeco.llvm.global.LLVM.LLVMBuildSub
import org.bytedeco.llvm.global.LLVM.LLVMBuildSwitch
import org.bytedeco.llvm.global.LLVM.LLVMBuildUDiv
import org.bytedeco.llvm.global.LLVM.LLVMBuildUIToFP
import org.bytedeco.llvm.global.LLVM.LLVMBuildUnreachable
import org.bytedeco.llvm.global.LLVM.LLVMBuildXor
import org.bytedeco.llvm.global.LLVM.LLVMCreateBuilderInContext
import org.bytedeco.llvm.global.LLVM.LLVMGetInsertBlock
import org.bytedeco.llvm.global.LLVM.LLVMPositionBuilder
import org.bytedeco.llvm.global.LLVM.LLVMPositionBuilderAtEnd
import org.bytedeco.llvm.global.LLVM.LLVMPositionBuilderBefore
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.BasicBlock
import org.llvm4j.llvm4j.BlockAddress
import org.llvm4j.llvm4j.BranchInstruction
import org.llvm4j.llvm4j.Builder
import org.llvm4j.llvm4j.Context
import org.llvm4j.llvm4j.FloatingPointType
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.IndirectBrInstruction
import org.llvm4j.llvm4j.Instruction
import org.llvm4j.llvm4j.IntegerType
import org.llvm4j.llvm4j.LoadInstruction
import org.llvm4j.llvm4j.PhiInstruction
import org.llvm4j.llvm4j.PointerType
import org.llvm4j.llvm4j.ReturnInstruction
import org.llvm4j.llvm4j.StoreInstruction
import org.llvm4j.llvm4j.SwitchInstruction
import org.llvm4j.llvm4j.Type
import org.llvm4j.llvm4j.UnreachableInstruction
import org.llvm4j.llvm4j.Value
import org.llvm4j.optional.None
import org.llvm4j.optional.Option
import org.llvm4j.optional.Some

fun Context.newBuilder(): Builder {
  return Builder(LLVMCreateBuilderInContext(ref))
}

val Builder.insertionBlock
  get() = LLVMGetInsertBlock(ref).asOption().map(::BasicBlock).toNullable()

fun Builder.position(label: BasicBlock, inst: Instruction) {
  LLVMPositionBuilder(ref, label.ref, inst.ref)
}

fun Builder.setPositionBefore(inst: Instruction) {
  LLVMPositionBuilderBefore(ref, inst.ref)
}

fun Builder.setPositionAtEnd(label: BasicBlock) {
  LLVMPositionBuilderAtEnd(ref, label.ref)
}

fun Builder.buildFPToUI(op: Value, type: IntegerType, name: Option<String> = None): Value {
  return when (name) {
    is Some -> Value(LLVMBuildFPToUI(ref, op.ref, type.ref, name.value))
    is None -> Value(LLVMBuildFPToUI(ref, op.ref, type.ref, null as String?))
  }
}

fun Builder.buildBranch(label: BasicBlock): BranchInstruction {
  return BranchInstruction(LLVMBuildBr(ref, label.ref))
}

fun Builder.buildCondBranch(
  cond: Value,
  ifTrue: BasicBlock,
  ifFalse: BasicBlock
): BranchInstruction {
  return BranchInstruction(LLVMBuildCondBr(ref, cond.ref, ifTrue.ref, ifFalse.ref))
}

fun Builder.buildSwitch(cond: Value, default: BasicBlock, expectedCases: Int): SwitchInstruction {
  return SwitchInstruction(LLVMBuildSwitch(ref, cond.ref, default.ref, expectedCases))
}

fun Builder.buildIndirectBranch(address: BlockAddress, expectedCases: Int): IndirectBrInstruction {
  return IndirectBrInstruction(LLVMBuildIndirectBr(ref, address.ref, expectedCases))
}

fun Builder.buildUnreachable(): UnreachableInstruction {
  return UnreachableInstruction(LLVMBuildUnreachable(ref))
}

fun Builder.buildFloatNeg(value: Value, name: String = ""): Value {
  return Value(LLVMBuildFNeg(ref, value.ref, name))
}

fun Builder.buildAdd(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildAdd(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildNSWAdd(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildNSWAdd(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildNUWAdd(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildNUWAdd(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildSub(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildSub(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildNSWSub(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildNSWSub(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildNUWSub(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildNUWSub(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildMul(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildMul(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildNSWMul(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildNSWMul(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildNUWMul(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildNUWMul(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildFMul(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildFMul(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildUDiv(dividend: Value, divisor: Value, name: String = ""): Value {
  return Value(LLVMBuildUDiv(ref, dividend.ref, divisor.ref, name))
}

fun Builder.buildExactUDiv(dividend: Value, divisor: Value, name: String = ""): Value {
  return Value(LLVMBuildExactUDiv(ref, dividend.ref, divisor.ref, name))
}

fun Builder.buildSDiv(dividend: Value, divisor: Value, name: String = ""): Value {
  return Value(LLVMBuildSDiv(ref, dividend.ref, divisor.ref, name))
}

fun Builder.buildFSub(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildFSub(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildFAdd(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildFAdd(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildFDiv(dividend: Value, divisor: Value, name: String = ""): Value {
  return Value(LLVMBuildFDiv(ref, dividend.ref, divisor.ref, name))
}

fun Builder.buildURem(dividend: Value, divisor: Value, name: String = ""): Value {
  return Value(LLVMBuildFRem(ref, dividend.ref, divisor.ref, name))
}

fun Builder.buildSRem(dividend: Value, divisor: Value, name: String = ""): Value {
  return Value(LLVMBuildSRem(ref, dividend.ref, divisor.ref, name))
}

fun Builder.buildFRem(dividend: Value, divisor: Value, name: String = ""): Value {
  return Value(LLVMBuildFRem(ref, dividend.ref, divisor.ref, name))
}

fun Builder.buildLShift(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildShl(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildLRShift(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildLShr(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildARShift(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildAShr(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildLAnd(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildAnd(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildLOr(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildOr(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildLXor(lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildXor(ref, lhs.ref, rhs.ref, name))
}

fun Builder.buildInsertElement(vec: Value, value: Value, index: Value, name: String = ""): Value {
  return Value(LLVMBuildInsertElement(ref, vec.ref, value.ref, index.ref, name))
}

fun Builder.buildExtractValue(aggregate: Value, index: Int, name: String = ""): Value {
  return Value(LLVMBuildExtractValue(ref, aggregate.ref, index, name))
}

fun Builder.buildExtractElement(aggregate: Value, index: Value, name: String = ""): Value {
  return Value(LLVMBuildExtractElement(ref, aggregate.ref, index.ref, name))
}

fun Builder.buildInsertValue(aggregate: Value, value: Value, index: Int, name: String = ""): Value {
  return Value(LLVMBuildInsertValue(ref, aggregate.ref, value.ref, index, name))
}

fun Builder.buildAlloca(type: Type, name: String = ""): AllocaInstruction {
  return AllocaInstruction(LLVMBuildAlloca(ref, type.ref, name))
}

fun Builder.buildLoad(ptr: Value, name: String = ""): LoadInstruction {
  return LoadInstruction(LLVMBuildLoad(ref, ptr.ref, name))
}

fun Builder.buildLoad(ptr: Value, type: Type, name: String = ""): LoadInstruction {
  return LoadInstruction(LLVMBuildLoad2(ref, type.ref, ptr.ref, name))
}

fun Builder.buildStore(ptr: Value, value: Value, name: String = ""): StoreInstruction {
  return StoreInstruction(LLVMBuildStore(ref, ptr.ref, value.ref))
}

fun Builder.buildFPToSI(op: Value, type: IntegerType, name: String = ""): Value {
  return Value(LLVMBuildFPToSI(ref, op.ref, type.ref, name))
}

fun Builder.buildSIToFP(op: Value, type: FloatingPointType, name: String = ""): Value {
  return Value(LLVMBuildSIToFP(ref, op.ref, type.ref, name))
}

fun Builder.buildFPToUI(op: Value, type: IntegerType, name: String = ""): Value {
  return Value(LLVMBuildFPToUI(ref, op.ref, type.ref, name))
}

fun Builder.buildUIToFP(op: Value, type: FloatingPointType, name: String = ""): Value {
  return Value(LLVMBuildUIToFP(ref, op.ref, type.ref, name))
}

fun Builder.buildGEP(
  aggregate: Value,
  indices: List<Value>,
  name: String = ""
): Value {
  return Value(
    LLVMBuildGEP(
      ref,
      aggregate.ref,
      indices.map(Value::ref).asPointer(),
      indices.size,
      name
    )
  )
}

fun Builder.buildInBoundsGEP(
  aggregate: Value,
  indices: List<Value>,
  name: String = ""
): Value {
  return Value(
    LLVMBuildInBoundsGEP(
      ref,
      aggregate.ref,
      indices.map(Value::ref).asPointer(),
      indices.size,
      name
    )
  )
}

fun Builder.buildGEP(
  aggregate: Value,
  type: Type,
  indices: List<Value>,
  name: String = ""
): Value {
  return Value(
    LLVMBuildGEP2(
      ref,
      type.ref,
      aggregate.ref,
      indices.map(Value::ref).asPointer(),
      indices.size,
      name
    )
  )
}

fun Builder.buildInBoundsGEP(
  aggregate: Value,
  type: Type,
  indices: List<Value>,
  name: String = ""
): Value {
  return Value(
    LLVMBuildInBoundsGEP2(
      ref,
      type.ref,
      aggregate.ref,
      indices.map(Value::ref).asPointer(),
      indices.size,
      name
    )
  )
}

fun Builder.buildIntToPtr(op: Value, type: FloatingPointType, name: String = ""): Value {
  return Value(LLVMBuildIntToPtr(ref, op.ref, type.ref, name))
}

fun Builder.buildPtrToInt(op: Value, type: IntegerType, name: String = ""): Value {
  return Value(LLVMBuildPtrToInt(ref, op.ref, type.ref, name))
}

fun Builder.buildBitCast(op: Value, type: Type, name: String = ""): Value {
  return Value(LLVMBuildBitCast(ref, op.ref, type.ref, name))
}

fun Builder.buildAddressSpaceCast(op: Value, type: PointerType, name: String = ""): Value {
  return Value(LLVMBuildAddrSpaceCast(ref, op.ref, type.ref, name))
}

fun Builder.buildICmp(pred: Int, lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildICmp(ref, pred, lhs.ref, rhs.ref, name))
}

fun Builder.buildFCmp(pred: Int, lhs: Value, rhs: Value, name: String = ""): Value {
  return Value(LLVMBuildFCmp(ref, pred, lhs.ref, rhs.ref, name))
}

fun Builder.buildPhi(type: Type, name: String = ""): PhiInstruction {
  return PhiInstruction(LLVMBuildPhi(ref, type.ref, name))
}

fun Builder.buildSelect(cond: Value, ifTrue: Value, ifFalse: Value, name: String = ""): Value {
  return Value(
    LLVMBuildSelect(ref, cond.ref, ifTrue.ref, ifFalse.ref, name)
  )
}

fun Builder.buildCall(
  function: Function,
  type: Type,
  arguments: List<Value>,
  name: String = ""
): Value {
  return Value(
    LLVMBuildCall2(
      ref,
      type.ref,
      function.ref,
      arguments.map(Value::ref).asPointer(),
      arguments.size,
      name
    )
  )
}

fun Builder.buildCall(function: Function, arguments: List<Value>, name: String = ""): Value {
  return Value(
    LLVMBuildCall(
      ref,
      function.ref,
      arguments.map(Value::ref).asPointer(),
      arguments.size,
      name
    )
  )
}

fun Builder.buildRet(value: Value): ReturnInstruction {
  return ReturnInstruction(LLVMBuildRet(ref, value.ref))
}

fun Builder.buildRet(): ReturnInstruction {
  return ReturnInstruction(LLVMBuildRetVoid(ref))
}
