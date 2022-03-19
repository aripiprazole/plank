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

package org.plank.llvm4k.ir

import llvm.LLVMOpcode

public actual enum class Opcode(public val llvm: LLVMOpcode) {
  Ret(LLVMOpcode.LLVMRet),
  Br(LLVMOpcode.LLVMBr),
  Switch(LLVMOpcode.LLVMSwitch),
  IndirectBr(LLVMOpcode.LLVMIndirectBr),
  Invoke(LLVMOpcode.LLVMInvoke),
  Unreachable(LLVMOpcode.LLVMUnreachable),
  Add(LLVMOpcode.LLVMAdd),
  FAdd(LLVMOpcode.LLVMFAdd),
  Sub(LLVMOpcode.LLVMSub),
  FSub(LLVMOpcode.LLVMFSub),
  Mul(LLVMOpcode.LLVMMul),
  FMul(LLVMOpcode.LLVMFMul),
  UDiv(LLVMOpcode.LLVMUDiv),
  SDiv(LLVMOpcode.LLVMSDiv),
  FDiv(LLVMOpcode.LLVMFDiv),
  URem(LLVMOpcode.LLVMURem),
  SRem(LLVMOpcode.LLVMSRem),
  FRem(LLVMOpcode.LLVMFRem),
  Shl(LLVMOpcode.LLVMShl),
  LShr(LLVMOpcode.LLVMLShr),
  AShr(LLVMOpcode.LLVMAShr),
  And(LLVMOpcode.LLVMAnd),
  Or(LLVMOpcode.LLVMOr),
  Xor(LLVMOpcode.LLVMXor),
  Alloca(LLVMOpcode.LLVMAlloca),
  Load(LLVMOpcode.LLVMLoad),
  Store(LLVMOpcode.LLVMStore),
  GetElementPtr(LLVMOpcode.LLVMGetElementPtr),
  Trunc(LLVMOpcode.LLVMTrunc),
  ZExt(LLVMOpcode.LLVMZExt),
  SExt(LLVMOpcode.LLVMSExt),
  FPToUI(LLVMOpcode.LLVMFPToUI),
  FPToSI(LLVMOpcode.LLVMFPToSI),
  UIToFP(LLVMOpcode.LLVMUIToFP),
  SIToFP(LLVMOpcode.LLVMSIToFP),
  FPTrunc(LLVMOpcode.LLVMFPTrunc),
  FPExt(LLVMOpcode.LLVMFPExt),
  PtrToInt(LLVMOpcode.LLVMPtrToInt),
  IntToPtr(LLVMOpcode.LLVMIntToPtr),
  BitCast(LLVMOpcode.LLVMBitCast),
  ICmp(LLVMOpcode.LLVMICmp),
  FCmp(LLVMOpcode.LLVMFCmp),
  PHI(LLVMOpcode.LLVMPHI),
  Call(LLVMOpcode.LLVMCall),
  Select(LLVMOpcode.LLVMSelect),
  UserOp1(LLVMOpcode.LLVMUserOp1),
  UserOp2(LLVMOpcode.LLVMUserOp2),
  VAArg(LLVMOpcode.LLVMVAArg),
  ExtractElement(LLVMOpcode.LLVMExtractElement),
  InsertElement(LLVMOpcode.LLVMInsertElement),
  ShuffleVector(LLVMOpcode.LLVMShuffleVector),
  ExtractValue(LLVMOpcode.LLVMExtractValue),
  InsertValue(LLVMOpcode.LLVMInsertValue),
  Fence(LLVMOpcode.LLVMFence),
  AtomicCmpXchg(LLVMOpcode.LLVMAtomicCmpXchg),
  AtomicRMW(LLVMOpcode.LLVMAtomicRMW),
  Resume(LLVMOpcode.LLVMResume),
  LandingPad(LLVMOpcode.LLVMLandingPad),
  AddrSpaceCast(LLVMOpcode.LLVMAddrSpaceCast),
  CleanupRet(LLVMOpcode.LLVMCleanupRet),
  CatchRet(LLVMOpcode.LLVMCatchRet),
  CatchPad(LLVMOpcode.LLVMCatchPad),
  CleanupPad(LLVMOpcode.LLVMCleanupPad),
  CatchSwitch(LLVMOpcode.LLVMCatchSwitch),
  FNeg(LLVMOpcode.LLVMFNeg),
  CallBr(LLVMOpcode.LLVMCallBr),
  Freeze(LLVMOpcode.LLVMFreeze);

  public actual val value: UInt get() = llvm.value

  public actual companion object {
    public fun byValue(llvm: LLVMOpcode): Opcode {
      return byValue(llvm.value)
    }

    public actual fun byValue(value: Int): Opcode {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): Opcode {
      return values().single { it.value == value }
    }
  }
}
