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

@file:Suppress("TooManyFunctions")

package org.plank.llvm4k

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
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

public interface IRBuilder : Disposable {
  public val insertionBlock: BasicBlock?

  public fun positionAfter(block: BasicBlock): IRBuilder

  public fun createGlobalStringPtr(
    value: String,
  ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, GlobalVariable>>

  public fun createGlobalStringPtr(value: String, name: String): GlobalVariable

  public fun createGlobalString(
    value: String,
  ): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, GlobalVariable>>

  public fun createGlobalString(value: String, name: String): GlobalVariable

  public fun createRetVoid(): ReturnInst

  public fun createRet(value: Value? = null): ReturnInst

  public fun createAggregateRet(vararg values: Value): ReturnInst {
    return createAggregateRet(values.toList())
  }

  public fun createAggregateRet(values: List<Value>): ReturnInst

  public fun createBr(dest: BasicBlock): BranchInst

  public fun createCondBr(cond: Value, ifTrue: BasicBlock, ifFalse: BasicBlock): BranchInst

  public fun createSwitch(value: Value, default: BasicBlock, numCases: Int = 10): SwitchInst

  public fun createIndirectBr(address: Value, numDests: Int = 10): IndirectBrInst

  public fun createInvoke(
    type: FunctionType,
    callee: Value,
    normalDest: BasicBlock,
    unwindDest: BasicBlock,
    vararg args: Value,
    name: String? = null,
  ): InvokeInst {
    return createInvoke(type, callee, normalDest, unwindDest, args.toList(), name)
  }

  public fun createInvoke(
    type: FunctionType,
    callee: Value,
    normalDest: BasicBlock,
    unwindDest: BasicBlock,
    args: List<Value>,
    name: String? = null,
  ): InvokeInst

  public fun createInvoke(
    callee: Value,
    normalDest: BasicBlock,
    unwindDest: BasicBlock,
    vararg args: Value,
    name: String? = null,
  ): InvokeInst {
    return createInvoke(callee, normalDest, unwindDest, args.toList(), name)
  }

  public fun createInvoke(
    callee: Value,
    normalDest: BasicBlock,
    unwindDest: BasicBlock,
    args: List<Value>,
    name: String? = null,
  ): InvokeInst

  public fun createResume(value: Value): ResumeInst

  public fun createCleanupRet(pad: CleanupPadInst, unwindBB: BasicBlock? = null): CleanupReturnInst

  public fun createCatchSwitch(
    parentPad: Value,
    unwindBB: BasicBlock,
    numHandlers: Int = 10,
    name: String? = null,
  ): CatchSwitchInst

  public fun createCatchPad(
    parentPad: Value,
    vararg args: Value,
    name: String? = null,
  ): CatchPadInst {
    return createCatchPad(parentPad, args.toList(), name)
  }

  public fun createCatchPad(parentPad: Value, args: List<Value>, name: String? = null): CatchPadInst

  public fun createCleanupPad(
    parentPad: Value,
    vararg args: Value,
    name: String? = null,
  ): CleanupPadInst {
    return createCleanupPad(parentPad, args.toList(), name)
  }

  public fun createCleanupPad(
    parentPad: Value,
    args: List<Value>,
    name: String? = null,
  ): CleanupPadInst

  public fun createCatchRet(catchPad: CatchPadInst, bb: BasicBlock): CatchReturnInst

  public fun createUnreachable(): UnreachableInst

  public fun createAdd(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createNSWAdd(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createNUWAdd(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createSub(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createNSWSub(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createNUWSub(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createMul(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createNSWMul(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createNUWMul(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createUDiv(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createExactUDiv(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createSDiv(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createExactSDiv(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createURem(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createSRem(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createShl(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createLShr(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createAShr(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createAnd(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createOr(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createXor(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createFAdd(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createFSub(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createFMul(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createFDiv(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createFRem(lhs: Value, rhs: Value, name: String? = null): Value

  public fun createBinOp(opcode: Opcode, lhs: Value, rhs: Value, name: String? = null): Value

  public fun createNeg(value: Value, name: String? = null): Value

  public fun createNSWNeg(value: Value, name: String? = null): Value

  public fun createNUWNeg(value: Value, name: String? = null): Value

  public fun createFNeg(value: Value, name: String? = null): Value

  public fun createNot(value: Value, name: String? = null): Value

  public fun createAlloca(
    type: Type,
    addrSpace: AddrSpace = AddrSpace.Generic,
    name: String? = null,
  ): AllocaInst

  public fun createArrayAlloca(
    type: Type,
    value: Value,
    addrSpace: AddrSpace = AddrSpace.Generic,
    name: String? = null,
  ): AllocaInst

  public fun createMalloc(
    type: Type,
    addrSpace: AddrSpace = AddrSpace.Generic,
    name: String? = null,
  ): CallInst

  public fun createArrayMalloc(
    type: Type,
    addrSpace: AddrSpace = AddrSpace.Generic,
    value: Value,
    name: String? = null,
  ): CallInst

  public fun createLoad(ptr: Value, name: String? = null): LoadInst

  public fun createLoad(type: Type, ptr: Value, name: String? = null): LoadInst

  public fun createStore(value: Value, ptr: Value): StoreInst

  public fun createFence(
    order: AtomicOrdering,
    singleThread: Boolean = true,
    name: String? = null,
  ): FenceInst

  public fun createAtomicCmpXchg(
    ptr: Value,
    cmp: Value,
    new: Value,
    successOrdering: AtomicOrdering,
    failOrdering: AtomicOrdering,
    singleThread: Boolean = true,
  ): AtomicCmpXchgInst

  public fun createAtomicRMW(
    opcode: AtomicRMWBinOp,
    ptr: Value,
    value: Value,
    order: AtomicOrdering,
    singleThread: Boolean = true,
  ): AtomicRMWInst

  public fun createGEP(
    type: Type,
    pointer: Value,
    vararg indices: Value,
    inBounds: Boolean = false,
    name: String? = null,
  ): Value {
    return createGEP(type, pointer, indices.toList(), inBounds, name)
  }

  public fun createGEP(
    type: Type,
    pointer: Value,
    indices: List<Value>,
    inBounds: Boolean = false,
    name: String? = null,
  ): Value

  public fun createGEP(
    pointer: Value,
    vararg indices: Value,
    inBounds: Boolean = false,
    name: String? = null,
  ): Value {
    return createGEP(pointer, indices.toList(), inBounds, name)
  }

  public fun createGEP(
    pointer: Value,
    indices: List<Value>,
    inBounds: Boolean = false,
    name: String? = null,
  ): Value

  public fun createStructGEP(
    pointer: Value,
    index: Int,
    name: String? = null,
  ): Value

  public fun createStructGEP(
    type: Type,
    pointer: Value,
    index: Int,
    name: String? = null,
  ): Value

  public fun createTrunc(value: Value, type: Type, name: String? = null): Value

  public fun createZExt(value: Value, type: Type, name: String? = null): Value

  public fun createSExt(value: Value, type: Type, name: String? = null): Value

  public fun createFPToUI(value: Value, type: Type, name: String? = null): Value

  public fun createFPToSI(value: Value, type: Type, name: String? = null): Value

  public fun createUIToFP(value: Value, type: Type, name: String? = null): Value

  public fun createSIToFP(value: Value, type: Type, name: String? = null): Value

  public fun createFPTrunc(value: Value, type: Type, name: String? = null): Value

  public fun createFPExt(value: Value, type: Type, name: String? = null): Value

  public fun createPtrToInt(value: Value, type: Type, name: String? = null): Value

  public fun createIntToPtr(value: Value, type: Type, name: String? = null): Value

  public fun createBitCast(value: Value, type: Type, name: String? = null): Value

  public fun createAddressSpaceCast(value: Value, type: Type, name: String? = null): Value

  public fun createZExtOrBitCast(value: Value, type: Type, name: String? = null): Value

  public fun createSExtOrBitCast(value: Value, type: Type, name: String? = null): Value

  public fun createTruncOrBitCast(value: Value, type: Type, name: String? = null): Value

  public fun createCast(opcode: Opcode, value: Value, type: Type, name: String? = null): Value

  public fun createPointerCast(value: Value, type: Type, name: String? = null): Value

  public fun createIntCast(
    value: Value,
    type: Type,
    isSigned: Boolean = true,
    name: String? = null,
  ): Value

  public fun createFPCast(value: Value, type: Type, name: String? = null): Value

  public fun createICmp(
    predicate: IntPredicate,
    lhs: Value,
    rhs: Value,
    name: String? = null,
  ): Value

  public fun createFCmp(
    predicate: RealPredicate,
    lhs: Value,
    rhs: Value,
    name: String? = null,
  ): Value

  public fun createCall(
    type: Type,
    func: Function,
    vararg arguments: Value,
    name: String? = null,
  ): CallInst {
    return createCall(type, func, arguments.toList(), name)
  }

  public fun createCall(
    type: Type,
    func: Function,
    arguments: List<Value>,
    name: String? = null,
  ): CallInst

  public fun createCall(func: Function, vararg arguments: Value, name: String? = null): CallInst {
    return createCall(func, arguments.toList(), name)
  }

  public fun createCall(func: Function, arguments: List<Value>, name: String? = null): CallInst

  public fun createSelect(cond: Value, ifTrue: Value, ifFalse: Value, name: String? = null): Value

  public fun createVAArg(list: Value, type: Type, name: String? = null): Value

  public fun createExtractElement(vec: Value, index: Value, name: String? = null): Value

  public fun createInsertElement(
    vec: Value,
    element: Value,
    index: Value,
    name: String? = null,
  ): Value

  public fun createShuffleVector(
    vec1: Value,
    vec2: Value,
    mask: Value,
    name: String? = null,
  ): Value

  public fun createExtractValue(aggregate: Value, index: Int, name: String? = null): Value

  public fun createInsertValue(
    aggregate: Value,
    element: Value,
    index: Int,
    name: String? = null,
  ): Value

  public fun createLandingPad(type: Type, numClauses: Int, name: String? = null): LandingPadInst

  public fun createFreeze(value: Value, name: String? = null): Value

  public fun createPhi(type: Type, name: String? = null): PhiInst

  public fun createIsNull(value: Value, name: String? = null): Value

  public fun createIsNotNull(value: Value, name: String? = null): Value
}
