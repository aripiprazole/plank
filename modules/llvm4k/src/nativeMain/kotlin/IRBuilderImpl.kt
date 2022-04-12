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

import kotlinx.cinterop.toCValues
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

internal class IRBuilderImpl(val ref: llvm.LLVMBuilderRef?) : IRBuilder {
  override val insertionBlock: BasicBlock?
    get(): BasicBlock? = llvm.LLVMGetInsertBlock(ref)?.let(::BasicBlock)

  override fun positionAfter(block: BasicBlock): IRBuilder {
    llvm.LLVMPositionBuilderAtEnd(ref, block.ref)
    return this
  }

  override fun createGlobalStringPtr(value: String): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, GlobalVariable>> {
    return PropertyDelegateProvider { _, property ->
      val ptr = createGlobalStringPtr(value, property.name)

      ReadOnlyProperty { _, _ -> ptr }
    }
  }

  override fun createGlobalStringPtr(value: String, name: String): GlobalVariable {
    return GlobalVariable(llvm.LLVMBuildGlobalStringPtr(ref, value, name))
  }

  override fun createGlobalString(value: String): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, GlobalVariable>> {
    return PropertyDelegateProvider { _, property ->
      val string = createGlobalString(value, property.name)

      ReadOnlyProperty { _, _ -> string }
    }
  }

  override fun createGlobalString(value: String, name: String): GlobalVariable {
    return GlobalVariable(llvm.LLVMBuildGlobalString(ref, value, name))
  }

  override fun createRetVoid(): ReturnInst {
    return ReturnInst(llvm.LLVMBuildRetVoid(ref))
  }

  override fun createRet(value: Value?): ReturnInst {
    return when (value) {
      null -> ReturnInst(llvm.LLVMBuildRetVoid(ref))
      else -> ReturnInst(llvm.LLVMBuildRet(ref, value.ref))
    }
  }

  override fun createAggregateRet(values: List<Value>): ReturnInst {
    return ReturnInst(
      llvm.LLVMBuildAggregateRet(ref, values.map { it.ref }.toCValues(), values.size.toUInt())
    )
  }

  override fun createBr(dest: BasicBlock): BranchInst {
    return BranchInst(llvm.LLVMBuildBr(ref, dest.ref))
  }

  override fun createCondBr(cond: Value, ifTrue: BasicBlock, ifFalse: BasicBlock): BranchInst {
    return BranchInst(llvm.LLVMBuildCondBr(ref, cond.ref, ifTrue.ref, ifFalse.ref))
  }

  override fun createSwitch(value: Value, default: BasicBlock, numCases: Int): SwitchInst {
    return SwitchInst(llvm.LLVMBuildSwitch(ref, value.ref, default.ref, numCases.toUInt()))
  }

  override fun createIndirectBr(address: Value, numDests: Int): IndirectBrInst {
    return IndirectBrInst(llvm.LLVMBuildIndirectBr(ref, address.ref, numDests.toUInt()))
  }

  override fun createInvoke(
    type: FunctionType,
    callee: Value,
    normalDest: BasicBlock,
    unwindDest: BasicBlock,
    args: List<Value>,
    name: String?,
  ): InvokeInst {
    val ref = llvm.LLVMBuildInvoke2(
      ref, type.ref, callee.ref, args.map { it.ref }.toCValues(), args.size.toUInt(),
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
    val ref = llvm.LLVMBuildInvoke(
      ref, callee.ref, args.map { it.ref }.toCValues(), args.size.toUInt(),
      normalDest.ref, unwindDest.ref, name ?: ""
    )

    return InvokeInst(ref)
  }

  override fun createResume(value: Value): ResumeInst {
    return ResumeInst(llvm.LLVMBuildResume(ref, value.ref))
  }

  override fun createCleanupRet(pad: CleanupPadInst, unwindBB: BasicBlock?): CleanupReturnInst {
    return CleanupReturnInst(llvm.LLVMBuildCleanupRet(ref, pad.ref, unwindBB?.ref))
  }

  override fun createCatchSwitch(
    parentPad: Value,
    unwindBB: BasicBlock,
    numHandlers: Int,
    name: String?,
  ): CatchSwitchInst {
    return CatchSwitchInst(
      llvm.LLVMBuildCatchSwitch(ref, parentPad.ref, unwindBB.ref, numHandlers.toUInt(), name ?: "")
    )
  }

  override fun createCatchPad(parentPad: Value, args: List<Value>, name: String?): CatchPadInst {
    val ref = llvm.LLVMBuildCatchPad(
      ref,
      parentPad.ref,
      args.map { it.ref }.toCValues(),
      args.size.toUInt(),
      name ?: "",
    )

    return CatchPadInst(ref)
  }

  override fun createCleanupPad(
    parentPad: Value,
    args: List<Value>,
    name: String?
  ): CleanupPadInst {
    val ref = llvm.LLVMBuildCleanupPad(
      ref,
      parentPad.ref,
      args.map { it.ref }.toCValues(), args.size.toUInt(),
      name ?: ""
    )

    return CleanupPadInst(ref)
  }

  override fun createCatchRet(catchPad: CatchPadInst, bb: BasicBlock): CatchReturnInst {
    return CatchReturnInst(llvm.LLVMBuildCatchRet(ref, catchPad.ref, bb.ref))
  }

  override fun createUnreachable(): UnreachableInst {
    return UnreachableInst(llvm.LLVMBuildUnreachable(ref))
  }

  override fun createAdd(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildAdd(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNSWAdd(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildNSWAdd(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNUWAdd(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildNUWAdd(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createSub(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildSub(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNSWSub(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildNSWSub(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNUWSub(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildNUWSub(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createMul(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildMul(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNSWMul(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildNSWMul(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNUWMul(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildNUWMul(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createUDiv(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildUDiv(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createExactUDiv(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildExactUDiv(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createSDiv(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildSDiv(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createExactSDiv(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildExactSDiv(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createURem(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildURem(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createSRem(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildSRem(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createShl(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildShl(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createLShr(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildLShr(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createAShr(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildAShr(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createAnd(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildAnd(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createOr(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildOr(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createXor(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildXor(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFAdd(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildFAdd(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFSub(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildFSub(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFMul(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildFMul(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFDiv(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildFDiv(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFRem(lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildFRem(ref, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createBinOp(opcode: Opcode, lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildBinOp(ref, opcode.llvm, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createNeg(value: Value, name: String?): Value {
    return Value(llvm.LLVMBuildNeg(ref, value.ref, name ?: ""))
  }

  override fun createNSWNeg(value: Value, name: String?): Value {
    return Value(llvm.LLVMBuildNSWNeg(ref, value.ref, name ?: ""))
  }

  override fun createNUWNeg(value: Value, name: String?): Value {
    return Value(llvm.LLVMBuildNUWNeg(ref, value.ref, name ?: ""))
  }

  override fun createFNeg(value: Value, name: String?): Value {
    return Value(llvm.LLVMBuildFNeg(ref, value.ref, name ?: ""))
  }

  override fun createNot(value: Value, name: String?): Value {
    return Value(llvm.LLVMBuildNot(ref, value.ref, name ?: ""))
  }

  override fun createAlloca(type: Type, addrSpace: AddrSpace, name: String?): AllocaInst {
    return AllocaInst(llvm.LLVMBuildAlloca(ref, type.ref, name ?: ""))
  }

  override fun createArrayAlloca(
    type: Type,
    value: Value,
    addrSpace: AddrSpace,
    name: String?,
  ): AllocaInst {
    return AllocaInst(llvm.LLVMBuildArrayAlloca(ref, type.ref, value.ref, name ?: ""))
  }

  override fun createMalloc(type: Type, addrSpace: AddrSpace, name: String?): CallInst {
    return CallInst(llvm.LLVMBuildMalloc(ref, type.ref, name ?: ""))
  }

  override fun createArrayMalloc(
    type: Type,
    addrSpace: AddrSpace,
    value: Value,
    name: String?
  ): CallInst {
    return CallInst(llvm.LLVMBuildArrayMalloc(ref, type.ref, value.ref, name ?: ""))
  }

  override fun createLoad(ptr: Value, name: String?): LoadInst {
    return LoadInst(llvm.LLVMBuildLoad(ref, ptr.ref, name ?: ""))
  }

  override fun createLoad(type: Type, ptr: Value, name: String?): LoadInst {
    return LoadInst(llvm.LLVMBuildLoad2(ref, type.ref, ptr.ref, name ?: ""))
  }

  override fun createStore(value: Value, ptr: Value): StoreInst {
    return StoreInst(llvm.LLVMBuildStore(ref, value.ref, ptr.ref))
  }

  override fun createFence(order: AtomicOrdering, singleThread: Boolean, name: String?): FenceInst {
    return FenceInst(llvm.LLVMBuildFence(ref, order.llvm, singleThread.toInt(), name ?: ""))
  }

  override fun createAtomicCmpXchg(
    ptr: Value,
    cmp: Value,
    new: Value,
    successOrdering: AtomicOrdering,
    failOrdering: AtomicOrdering,
    singleThread: Boolean,
  ): AtomicCmpXchgInst {
    val ref = llvm.LLVMBuildAtomicCmpXchg(
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
    singleThread: Boolean
  ): AtomicRMWInst {
    val ref = llvm.LLVMBuildAtomicRMW(
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
    name: String?
  ): Value {
    val ref = when {
      inBounds -> llvm.LLVMBuildInBoundsGEP(
        ref,
        pointer.ref,
        indices.map { it.ref }.toCValues(),
        indices.size.toUInt(),
        name ?: ""
      )
      else -> llvm.LLVMBuildGEP(
        ref,
        pointer.ref,
        indices.map { it.ref }.toCValues(),
        indices.size.toUInt(),
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
    name: String?
  ): Value {
    val ref = when {
      inBounds -> llvm.LLVMBuildInBoundsGEP2(
        ref,
        type.ref,
        pointer.ref,
        indices.map { it.ref }.toCValues(),
        indices.size.toUInt(),
        name ?: ""
      )
      else -> llvm.LLVMBuildGEP2(
        ref,
        type.ref,
        pointer.ref,
        indices.map { it.ref }.toCValues(),
        indices.size.toUInt(),
        name ?: ""
      )
    }

    return Value(ref)
  }

  override fun createStructGEP(pointer: Value, index: Int, name: String?): Value {
    return Value(llvm.LLVMBuildStructGEP(ref, pointer.ref, index.toUInt(), name ?: ""))
  }

  override fun createStructGEP(type: Type, pointer: Value, index: Int, name: String?): Value {
    return Value(llvm.LLVMBuildStructGEP2(ref, type.ref, pointer.ref, index.toUInt(), name ?: ""))
  }

  override fun createTrunc(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildTrunc(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createZExt(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildZExt(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createSExt(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildSExt(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createFPToUI(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildFPToUI(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createFPToSI(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildFPToSI(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createUIToFP(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildUIToFP(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createSIToFP(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildSIToFP(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createFPTrunc(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildFPTrunc(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createFPExt(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildFPExt(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createPtrToInt(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildPtrToInt(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createIntToPtr(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildIntToPtr(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createBitCast(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildBitCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createAddressSpaceCast(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildAddrSpaceCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createZExtOrBitCast(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildZExtOrBitCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createSExtOrBitCast(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildSExtOrBitCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createTruncOrBitCast(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildTruncOrBitCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createCast(opcode: Opcode, value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildCast(ref, opcode.llvm, value.ref, type.ref, name ?: ""))
  }

  override fun createPointerCast(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildPointerCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createIntCast(value: Value, type: Type, isSigned: Boolean, name: String?): Value {
    return Value(llvm.LLVMBuildIntCast2(ref, value.ref, type.ref, isSigned.toInt(), name ?: ""))
  }

  override fun createFPCast(value: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildFPCast(ref, value.ref, type.ref, name ?: ""))
  }

  override fun createICmp(predicate: IntPredicate, lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildICmp(ref, predicate.llvm, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createFCmp(predicate: RealPredicate, lhs: Value, rhs: Value, name: String?): Value {
    return Value(llvm.LLVMBuildFCmp(ref, predicate.llvm, lhs.ref, rhs.ref, name ?: ""))
  }

  override fun createCall(
    type: Type,
    func: Function,
    arguments: List<Value>,
    name: String?,
  ): CallInst {
    val ref = llvm.LLVMBuildCall2(
      ref,
      type.ref,
      func.ref,
      arguments.map { it.ref }.toCValues(),
      arguments.size.toUInt(),
      name ?: ""
    )

    return CallInst(ref)
  }

  override fun createCall(func: Function, arguments: List<Value>, name: String?): CallInst {
    val ref = llvm.LLVMBuildCall(
      ref,
      func.ref,
      arguments.map { it.ref }.toCValues(),
      arguments.size.toUInt(),
      name ?: ""
    )

    return CallInst(ref)
  }

  override fun createSelect(cond: Value, ifTrue: Value, ifFalse: Value, name: String?): Value {
    return Value(llvm.LLVMBuildSelect(ref, cond.ref, ifTrue.ref, ifFalse.ref, name ?: ""))
  }

  override fun createVAArg(list: Value, type: Type, name: String?): Value {
    return Value(llvm.LLVMBuildVAArg(ref, list.ref, type.ref, name ?: ""))
  }

  override fun createExtractElement(vec: Value, index: Value, name: String?): Value {
    return Value(llvm.LLVMBuildExtractElement(ref, vec.ref, index.ref, name ?: ""))
  }

  override fun createInsertElement(vec: Value, element: Value, index: Value, name: String?): Value {
    return Value(llvm.LLVMBuildInsertElement(ref, vec.ref, element.ref, index.ref, name ?: ""))
  }

  override fun createShuffleVector(vec1: Value, vec2: Value, mask: Value, name: String?): Value {
    return Value(llvm.LLVMBuildShuffleVector(ref, vec1.ref, vec2.ref, mask.ref, name ?: ""))
  }

  override fun createExtractValue(aggregate: Value, index: Int, name: String?): Value {
    return Value(llvm.LLVMBuildExtractValue(ref, aggregate.ref, index.toUInt(), name ?: ""))
  }

  override fun createInsertValue(
    aggregate: Value,
    element: Value,
    index: Int,
    name: String?,
  ): Value {
    val ref = llvm.LLVMBuildInsertValue(ref, aggregate.ref, element.ref, index.toUInt(), name ?: "")

    return Value(ref)
  }

  override fun createLandingPad(type: Type, numClauses: Int, name: String?): LandingPadInst {
    val ref = llvm.LLVMBuildLandingPad(ref, type.ref, null, numClauses.toUInt(), name ?: "")

    return LandingPadInst(ref)
  }

  override fun createFreeze(value: Value, name: String?): Value {
    return Value(llvm.LLVMBuildFreeze(ref, value.ref, name ?: ""))
  }

  override fun createPhi(type: Type, name: String?): PhiInst {
    return PhiInst(llvm.LLVMBuildPhi(ref, type.ref, name ?: ""))
  }

  override fun createIsNull(value: Value, name: String?): Value {
    return Value(llvm.LLVMBuildIsNull(ref, value.ref, name ?: ""))
  }

  override fun createIsNotNull(value: Value, name: String?): Value {
    return Value(llvm.LLVMBuildIsNotNull(ref, value.ref, name ?: ""))
  }

  override fun close() {
    llvm.LLVMDisposeBuilder(ref)
  }

  override fun toString(): String = "IRBuilder"
}
