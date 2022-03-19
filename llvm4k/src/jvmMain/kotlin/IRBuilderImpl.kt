/*
 *    Copyright 2022 Plank
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.plank.llvm4k

import org.bytedeco.llvm.LLVM.LLVMBuilderRef
import org.bytedeco.llvm.global.LLVM
import org.plank.llvm4k.ir.AddrSpace
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.AtomicCmpXchgInst
import org.plank.llvm4k.ir.AtomicOrdering
import org.plank.llvm4k.ir.AtomicRMWBinOp
import org.plank.llvm4k.ir.AtomicRMWInst
import org.plank.llvm4k.ir.BasicBlock
import org.plank.llvm4k.ir.BranchInst
import org.plank.llvm4k.ir.CallInst
import org.plank.llvm4k.ir.CatchPadInst
import org.plank.llvm4k.ir.CatchReturnInst
import org.plank.llvm4k.ir.CatchSwitchInst
import org.plank.llvm4k.ir.CleanupPadInst
import org.plank.llvm4k.ir.CleanupReturnInst
import org.plank.llvm4k.ir.FenceInst
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.GlobalVariable
import org.plank.llvm4k.ir.IndirectBrInst
import org.plank.llvm4k.ir.IntPredicate
import org.plank.llvm4k.ir.InvokeInst
import org.plank.llvm4k.ir.LandingPadInst
import org.plank.llvm4k.ir.LoadInst
import org.plank.llvm4k.ir.Opcode
import org.plank.llvm4k.ir.PhiInst
import org.plank.llvm4k.ir.RealPredicate
import org.plank.llvm4k.ir.ResumeInst
import org.plank.llvm4k.ir.ReturnInst
import org.plank.llvm4k.ir.StoreInst
import org.plank.llvm4k.ir.SwitchInst
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.UnreachableInst
import org.plank.llvm4k.ir.Value
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

internal class IRBuilderImpl(val ref: LLVMBuilderRef?) : IRBuilder {
  override val insertionBlock: BasicBlock?
    get(): BasicBlock? = LLVM.LLVMGetInsertBlock(ref)?.let(::BasicBlock)

  override fun positionAfter(block: BasicBlock): IRBuilder {
    LLVM.LLVMPositionBuilderAtEnd(ref, block.ref)
    return this
  }

  override fun createGlobalStringPtr(value: String): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, GlobalVariable>> {
    return PropertyDelegateProvider { _, property ->
      val ptr = createGlobalStringPtr(value, property.name)

      ReadOnlyProperty { _, _ -> ptr }
    }
  }

  override fun createGlobalStringPtr(value: String, name: String): GlobalVariable {
    return GlobalVariable(LLVM.LLVMBuildGlobalStringPtr(ref, value, name))
  }

  override fun createGlobalString(value: String): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, GlobalVariable>> {
    return PropertyDelegateProvider { _, property ->
      val string = createGlobalString(value, property.name)

      ReadOnlyProperty { _, _ -> string }
    }
  }

  override fun createGlobalString(value: String, name: String): GlobalVariable {
    return GlobalVariable(LLVM.LLVMBuildGlobalString(ref, value, name))
  }

  override fun createRetVoid(): ReturnInst {
    return ReturnInst(LLVM.LLVMBuildRetVoid(ref))
  }

  override fun createRet(value: Value?): ReturnInst {
    return when (value) {
      null -> ReturnInst(LLVM.LLVMBuildRetVoid(ref))
      else -> ReturnInst(LLVM.LLVMBuildRet(ref, value.ref))
    }
  }

  override fun createAggregateRet(values: List<Value>): ReturnInst {
    return ReturnInst(
      LLVM.LLVMBuildAggregateRet(
        ref,
        values.map { it.ref }.toPointerPointer(),
        values.size
      )
    )
  }

  override fun createBr(dest: BasicBlock): BranchInst {
    return BranchInst(LLVM.LLVMBuildBr(ref, dest.ref))
  }

  override fun createCondBr(cond: Value, ifTrue: BasicBlock, ifFalse: BasicBlock): BranchInst {
    return BranchInst(LLVM.LLVMBuildCondBr(ref, cond.ref, ifTrue.ref, ifFalse.ref))
  }

  override fun createSwitch(value: Value, default: BasicBlock, numCases: Int): SwitchInst {
    return SwitchInst(LLVM.LLVMBuildSwitch(ref, value.ref, default.ref, numCases))
  }

  override fun createIndirectBr(address: Value, numDests: Int): IndirectBrInst {
    return IndirectBrInst(LLVM.LLVMBuildIndirectBr(ref, address.ref, numDests))
  }

  override fun createInvoke(
    type: FunctionType,
    callee: Value,
    normalDest: BasicBlock,
    unwindDest: BasicBlock,
    args: List<Value>,
    name: String?,
  ): InvokeInst {
    val ref = LLVM.LLVMBuildInvoke2(
      ref, type.ref, callee.ref, args.map { it.ref }.toPointerPointer(), args.size,
      normalDest.ref, unwindDest.ref, name ?: ""
    )

    return InvokeInst(ref)
  }

  override fun createInvoke(
    callee: Value,
    normalDest: BasicBlock,
    unwindDest: BasicBlock,
    args: List<Value>,
    name: String?,
  ): InvokeInst {
    val ref = LLVM.LLVMBuildInvoke(
      ref, callee.ref, args.map { it.ref }.toPointerPointer(), args.size,
      normalDest.ref, unwindDest.ref, name ?: ""
    )

    return InvokeInst(ref)
  }

  override fun createResume(value: Value): ResumeInst {
    return ResumeInst(LLVM.LLVMBuildResume(ref, value.ref))
  }

  override fun createCleanupRet(pad: CleanupPadInst, unwindBB: BasicBlock?): CleanupReturnInst {
    return CleanupReturnInst(LLVM.LLVMBuildCleanupRet(ref, pad.ref, unwindBB?.ref))
  }

  override fun createCatchSwitch(
    parentPad: Value,
    unwindBB: BasicBlock,
    numHandlers: Int,
    name: String?,
  ): CatchSwitchInst {
    return CatchSwitchInst(
      LLVM.LLVMBuildCatchSwitch(ref, parentPad.ref, unwindBB.ref, numHandlers, name ?: "")
    )
  }

  override fun createCatchPad(parentPad: Value, args: List<Value>, name: String?): CatchPadInst {
    val ref = LLVM.LLVMBuildCatchPad(
      ref,
      parentPad.ref,
      args.map { it.ref }.toPointerPointer(),
      args.size,
      name ?: "",
    )

    return CatchPadInst(ref)
  }

  override fun createCleanupPad(
    parentPad: Value,
    args: List<Value>,
    name: String?,
  ): CleanupPadInst {
    val ref = LLVM.LLVMBuildCleanupPad(
      ref,
      parentPad.ref,
      args.map { it.ref }.toPointerPointer(), args.size,
      name ?: ""
    )

    return CleanupPadInst(ref)
  }

  override fun createCatchRet(catchPad: CatchPadInst, bb: BasicBlock): CatchReturnInst {
    return CatchReturnInst(LLVM.LLVMBuildCatchRet(ref, catchPad.ref, bb.ref))
  }

  override fun createUnreachable(): UnreachableInst {
    return UnreachableInst(LLVM.LLVMBuildUnreachable(ref))
  }

  override fun createAdd(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildAdd(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNSWAdd(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildNSWAdd(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNUWAdd(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildNUWAdd(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createSub(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildSub(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNSWSub(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildNSWSub(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNUWSub(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildNUWSub(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createMul(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildMul(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNSWMul(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildNSWMul(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNUWMul(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildNUWMul(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createUDiv(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildUDiv(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createExactUDiv(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildExactUDiv(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createSDiv(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildSDiv(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createExactSDiv(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildExactSDiv(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createURem(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildURem(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createSRem(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildSRem(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createShl(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildShl(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createLShr(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildLShr(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createAShr(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildAShr(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createAnd(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildAnd(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createOr(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildOr(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createXor(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildXor(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFAdd(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildFAdd(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFSub(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildFSub(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFMul(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildFMul(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFDiv(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildFDiv(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFRem(lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildFRem(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createBinOp(opcode: Opcode, lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildBinOp(ref, opcode.llvm, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNeg(value: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildNeg(ref, value.ref, name ?: ""))
  }

  override fun createNSWNeg(value: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildNSWNeg(ref, value.ref, name ?: ""))
  }

  override fun createNUWNeg(value: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildNUWNeg(ref, value.ref, name ?: ""))
  }

  override fun createFNeg(value: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildFNeg(ref, value.ref, name ?: ""))
  }

  override fun createNot(value: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildNot(ref, value.ref, name ?: ""))
  }

  override fun createAlloca(type: Type, addrSpace: AddrSpace, name: String?): AllocaInst {
    return AllocaInst(LLVM.LLVMBuildAlloca(ref, type.ref, name ?: ""))
  }

  override fun createArrayAlloca(
    type: Type,
    value: Value,
    addrSpace: AddrSpace,
    name: String?,
  ): AllocaInst {
    return AllocaInst(LLVM.LLVMBuildArrayAlloca(ref, type.ref, value.ref, name ?: ""))
  }

  override fun createMalloc(type: Type, addrSpace: AddrSpace, name: String?): CallInst {
    return CallInst(LLVM.LLVMBuildMalloc(ref, type.ref, name ?: ""))
  }

  override fun createArrayMalloc(
    type: Type,
    addrSpace: AddrSpace,
    value: Value,
    name: String?,
  ): CallInst {
    return CallInst(LLVM.LLVMBuildArrayMalloc(ref, type.ref, value.ref, name ?: ""))
  }

  override fun createLoad(ptr: Value, name: String?): LoadInst {
    return LoadInst(LLVM.LLVMBuildLoad(ref, ptr.ref, name ?: ""))
  }

  override fun createLoad(type: Type, ptr: Value, name: String?): LoadInst {
    return LoadInst(LLVM.LLVMBuildLoad2(ref, type.ref, ptr.ref, name ?: ""))
  }

  override fun createStore(value: Value, ptr: Value): StoreInst {
    return StoreInst(LLVM.LLVMBuildStore(ref, value.ref, ptr.ref))
  }

  override fun createFence(order: AtomicOrdering, singleThread: Boolean, name: String?): FenceInst {
    return FenceInst(LLVM.LLVMBuildFence(ref, order.llvm, singleThread.toInt(), name ?: ""))
  }

  override fun createAtomicCmpXchg(
    ptr: Value,
    cmp: Value,
    new: Value,
    successOrdering: AtomicOrdering,
    failOrdering: AtomicOrdering,
    singleThread: Boolean,
  ): AtomicCmpXchgInst {
    val ref = LLVM.LLVMBuildAtomicCmpXchg(
      ref,
      ptr.ref,
      cmp.ref,
      new.ref,
      successOrdering.llvm,
      failOrdering.llvm,
      singleThread.toInt()
    )

    return AtomicCmpXchgInst(ref)
  }

  override fun createAtomicRMW(
    opcode: AtomicRMWBinOp,
    ptr: Value,
    value: Value,
    order: AtomicOrdering,
    singleThread: Boolean,
  ): AtomicRMWInst {
    val ref = LLVM.LLVMBuildAtomicRMW(
      ref,
      opcode.llvm,
      ptr.ref,
      value.ref,
      order.llvm,
      singleThread.toInt()
    )

    return AtomicRMWInst(ref)
  }

  override fun createGEP(
    pointer: Value,
    indices: List<Value>,
    inBounds: Boolean,
    name: String?,
  ): Value {
    val ref = when {
      inBounds -> LLVM.LLVMBuildInBoundsGEP(
        ref,
        pointer.ref,
        indices.map { it.ref }.toPointerPointer(),
        indices.size,
        name ?: ""
      )
      else -> LLVM.LLVMBuildGEP(
        ref,
        pointer.ref,
        indices.map { it.ref }.toPointerPointer(),
        indices.size,
        name ?: ""
      )
    }

    return Value(ref)
  }

  override fun createGEP(
    type: Type,
    pointer: Value,
    indices: List<Value>,
    inBounds: Boolean,
    name: String?,
  ): Value {
    val ref = when {
      inBounds -> LLVM.LLVMBuildInBoundsGEP2(
        ref,
        type.ref,
        pointer.ref,
        indices.map { it.ref }.toPointerPointer(),
        indices.size,
        name ?: ""
      )
      else -> LLVM.LLVMBuildGEP2(
        ref,
        type.ref,
        pointer.ref,
        indices.map { it.ref }.toPointerPointer(),
        indices.size,
        name ?: ""
      )
    }

    return Value(ref)
  }

  override fun createStructGEP(pointer: Value, index: Int, name: String?): Value {
    return Value(LLVM.LLVMBuildStructGEP(ref, pointer.ref, index, name ?: ""))
  }

  override fun createStructGEP(type: Type, pointer: Value, index: Int, name: String?): Value {
    return Value(LLVM.LLVMBuildStructGEP2(ref, type.ref, pointer.ref, index, name ?: ""))
  }

  override fun createTrunc(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildTrunc(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createZExt(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildZExt(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createSExt(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildSExt(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createFPToUI(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildFPToUI(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createFPToSI(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildFPToSI(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createUIToFP(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildUIToFP(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createSIToFP(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildSIToFP(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createFPTrunc(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildFPTrunc(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createFPExt(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildFPExt(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createPtrToInt(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildPtrToInt(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createIntToPtr(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildIntToPtr(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createBitCast(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildBitCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createAddressSpaceCast(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildAddrSpaceCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createZExtOrBitCast(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildZExtOrBitCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createSExtOrBitCast(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildSExtOrBitCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createTruncOrBitCast(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildTruncOrBitCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createCast(opcode: Opcode, value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildCast(ref, opcode.llvm, value.ref, type.ref, name ?: ""))
  }

  override fun createPointerCast(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildPointerCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createIntCast(value: Value, type: Type, isSigned: Boolean, name: String?): Value {
    return Value(LLVM.LLVMBuildIntCast2(ref, value.ref, type.ref, isSigned.toInt(), name ?: ""))
  }

  override fun createFPCast(value: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildFPCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createICmp(predicate: IntPredicate, lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildICmp(ref, predicate.llvm, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFCmp(predicate: RealPredicate, lhs: Value, rhs: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildFCmp(ref, predicate.llvm, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createCall(
    type: Type,
    func: Function,
    arguments: List<Value>,
    name: String?,
  ): CallInst {
    val ref = LLVM.LLVMBuildCall2(
      ref,
      type.ref,
      func.ref,
      arguments.map { it.ref }.toPointerPointer(),
      arguments.size,
      name ?: ""
    )

    return CallInst(ref)
  }

  override fun createCall(func: Function, arguments: List<Value>, name: String?): CallInst {
    val ref = LLVM.LLVMBuildCall(
      ref,
      func.ref,
      arguments.map { it.ref }.toPointerPointer(),
      arguments.size,
      name ?: ""
    )

    return CallInst(ref)
  }

  override fun createSelect(cond: Value, ifTrue: Value, ifFalse: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildSelect(ref, cond.ref, ifTrue.ref, ifFalse.ref, name ?: ""))
  }

  override fun createVAArg(list: Value, type: Type, name: String?): Value {
    return Value(LLVM.LLVMBuildVAArg(ref, list.ref, type.ref, name ?: ""))
  }

  override fun createExtractElement(vec: Value, index: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildExtractElement(ref, vec.ref, index.ref, name ?: ""))
  }

  override fun createInsertElement(vec: Value, element: Value, index: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildInsertElement(ref, vec.ref, element.ref, index.ref, name ?: ""))
  }

  override fun createShuffleVector(vec1: Value, vec2: Value, mask: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildShuffleVector(ref, vec1.ref, vec2.ref, mask.ref, name ?: ""))
  }

  override fun createExtractValue(aggregate: Value, index: Int, name: String?): Value {
    return Value(LLVM.LLVMBuildExtractValue(ref, aggregate.ref, index, name ?: ""))
  }

  override fun createInsertValue(
    aggregate: Value,
    element: Value,
    index: Int,
    name: String?,
  ): Value {
    val ref = LLVM.LLVMBuildInsertValue(ref, aggregate.ref, element.ref, index, name ?: "")

    return Value(ref)
  }

  override fun createLandingPad(type: Type, numClauses: Int, name: String?): LandingPadInst {
    val ref = LLVM.LLVMBuildLandingPad(ref, type.ref, null, numClauses, name ?: "")

    return LandingPadInst(ref)
  }

  override fun createFreeze(value: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildFreeze(ref, value.ref, name ?: ""))
  }

  override fun createPhi(type: Type, name: String?): PhiInst {
    return PhiInst(LLVM.LLVMBuildPhi(ref, type.ref, name ?: ""))
  }

  override fun createIsNull(value: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildIsNull(ref, value.ref, name ?: ""))
  }

  override fun createIsNotNull(value: Value, name: String?): Value {
    return Value(LLVM.LLVMBuildIsNotNull(ref, value.ref, name ?: ""))
  }

  override fun close() {
    LLVM.LLVMDisposeBuilder(ref)
  }

  override fun toString(): String = "IRBuilder"
}
