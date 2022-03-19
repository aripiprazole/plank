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

import org.bytedeco.llvm.global.LLVM.LLVMAShr
import org.bytedeco.llvm.global.LLVM.LLVMAdd
import org.bytedeco.llvm.global.LLVM.LLVMAddrSpaceCast
import org.bytedeco.llvm.global.LLVM.LLVMAlloca
import org.bytedeco.llvm.global.LLVM.LLVMAnd
import org.bytedeco.llvm.global.LLVM.LLVMAtomicCmpXchg
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMW
import org.bytedeco.llvm.global.LLVM.LLVMBitCast
import org.bytedeco.llvm.global.LLVM.LLVMBr
import org.bytedeco.llvm.global.LLVM.LLVMCall
import org.bytedeco.llvm.global.LLVM.LLVMCallBr
import org.bytedeco.llvm.global.LLVM.LLVMCatchPad
import org.bytedeco.llvm.global.LLVM.LLVMCatchRet
import org.bytedeco.llvm.global.LLVM.LLVMCatchSwitch
import org.bytedeco.llvm.global.LLVM.LLVMCleanupPad
import org.bytedeco.llvm.global.LLVM.LLVMCleanupRet
import org.bytedeco.llvm.global.LLVM.LLVMExtractElement
import org.bytedeco.llvm.global.LLVM.LLVMExtractValue
import org.bytedeco.llvm.global.LLVM.LLVMFAdd
import org.bytedeco.llvm.global.LLVM.LLVMFCmp
import org.bytedeco.llvm.global.LLVM.LLVMFDiv
import org.bytedeco.llvm.global.LLVM.LLVMFMul
import org.bytedeco.llvm.global.LLVM.LLVMFNeg
import org.bytedeco.llvm.global.LLVM.LLVMFPExt
import org.bytedeco.llvm.global.LLVM.LLVMFPToSI
import org.bytedeco.llvm.global.LLVM.LLVMFPToUI
import org.bytedeco.llvm.global.LLVM.LLVMFPTrunc
import org.bytedeco.llvm.global.LLVM.LLVMFRem
import org.bytedeco.llvm.global.LLVM.LLVMFSub
import org.bytedeco.llvm.global.LLVM.LLVMFence
import org.bytedeco.llvm.global.LLVM.LLVMFreeze
import org.bytedeco.llvm.global.LLVM.LLVMGetElementPtr
import org.bytedeco.llvm.global.LLVM.LLVMICmp
import org.bytedeco.llvm.global.LLVM.LLVMIndirectBr
import org.bytedeco.llvm.global.LLVM.LLVMInsertElement
import org.bytedeco.llvm.global.LLVM.LLVMInsertValue
import org.bytedeco.llvm.global.LLVM.LLVMIntToPtr
import org.bytedeco.llvm.global.LLVM.LLVMInvoke
import org.bytedeco.llvm.global.LLVM.LLVMLShr
import org.bytedeco.llvm.global.LLVM.LLVMLandingPad
import org.bytedeco.llvm.global.LLVM.LLVMLoad
import org.bytedeco.llvm.global.LLVM.LLVMMul
import org.bytedeco.llvm.global.LLVM.LLVMOr
import org.bytedeco.llvm.global.LLVM.LLVMPHI
import org.bytedeco.llvm.global.LLVM.LLVMPtrToInt
import org.bytedeco.llvm.global.LLVM.LLVMResume
import org.bytedeco.llvm.global.LLVM.LLVMRet
import org.bytedeco.llvm.global.LLVM.LLVMSDiv
import org.bytedeco.llvm.global.LLVM.LLVMSExt
import org.bytedeco.llvm.global.LLVM.LLVMSIToFP
import org.bytedeco.llvm.global.LLVM.LLVMSRem
import org.bytedeco.llvm.global.LLVM.LLVMSelect
import org.bytedeco.llvm.global.LLVM.LLVMShl
import org.bytedeco.llvm.global.LLVM.LLVMShuffleVector
import org.bytedeco.llvm.global.LLVM.LLVMStore
import org.bytedeco.llvm.global.LLVM.LLVMSub
import org.bytedeco.llvm.global.LLVM.LLVMSwitch
import org.bytedeco.llvm.global.LLVM.LLVMTrunc
import org.bytedeco.llvm.global.LLVM.LLVMUDiv
import org.bytedeco.llvm.global.LLVM.LLVMUIToFP
import org.bytedeco.llvm.global.LLVM.LLVMURem
import org.bytedeco.llvm.global.LLVM.LLVMUnreachable
import org.bytedeco.llvm.global.LLVM.LLVMUserOp1
import org.bytedeco.llvm.global.LLVM.LLVMUserOp2
import org.bytedeco.llvm.global.LLVM.LLVMVAArg
import org.bytedeco.llvm.global.LLVM.LLVMXor
import org.bytedeco.llvm.global.LLVM.LLVMZExt

public actual enum class Opcode(public val llvm: Int) {
  Ret(LLVMRet),
  Br(LLVMBr),
  Switch(LLVMSwitch),
  IndirectBr(LLVMIndirectBr),
  Invoke(LLVMInvoke),
  Unreachable(LLVMUnreachable),
  Add(LLVMAdd),
  FAdd(LLVMFAdd),
  Sub(LLVMSub),
  FSub(LLVMFSub),
  Mul(LLVMMul),
  FMul(LLVMFMul),
  UDiv(LLVMUDiv),
  SDiv(LLVMSDiv),
  FDiv(LLVMFDiv),
  URem(LLVMURem),
  SRem(LLVMSRem),
  FRem(LLVMFRem),
  Shl(LLVMShl),
  LShr(LLVMLShr),
  AShr(LLVMAShr),
  And(LLVMAnd),
  Or(LLVMOr),
  Xor(LLVMXor),
  Alloca(LLVMAlloca),
  Load(LLVMLoad),
  Store(LLVMStore),
  GetElementPtr(LLVMGetElementPtr),
  Trunc(LLVMTrunc),
  ZExt(LLVMZExt),
  SExt(LLVMSExt),
  FPToUI(LLVMFPToUI),
  FPToSI(LLVMFPToSI),
  UIToFP(LLVMUIToFP),
  SIToFP(LLVMSIToFP),
  FPTrunc(LLVMFPTrunc),
  FPExt(LLVMFPExt),
  PtrToInt(LLVMPtrToInt),
  IntToPtr(LLVMIntToPtr),
  BitCast(LLVMBitCast),
  ICmp(LLVMICmp),
  FCmp(LLVMFCmp),
  PHI(LLVMPHI),
  Call(LLVMCall),
  Select(LLVMSelect),
  UserOp1(LLVMUserOp1),
  UserOp2(LLVMUserOp2),
  VAArg(LLVMVAArg),
  ExtractElement(LLVMExtractElement),
  InsertElement(LLVMInsertElement),
  ShuffleVector(LLVMShuffleVector),
  ExtractValue(LLVMExtractValue),
  InsertValue(LLVMInsertValue),
  Fence(LLVMFence),
  AtomicCmpXchg(LLVMAtomicCmpXchg),
  AtomicRMW(LLVMAtomicRMW),
  Resume(LLVMResume),
  LandingPad(LLVMLandingPad),
  AddrSpaceCast(LLVMAddrSpaceCast),
  CleanupRet(LLVMCleanupRet),
  CatchRet(LLVMCatchRet),
  CatchPad(LLVMCatchPad),
  CleanupPad(LLVMCleanupPad),
  CatchSwitch(LLVMCatchSwitch),
  FNeg(LLVMFNeg),
  CallBr(LLVMCallBr),
  Freeze(LLVMFreeze);

  public actual val value: UInt get() = llvm.toUInt()

  public actual companion object {
    public actual fun byValue(value: Int): Opcode {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): Opcode {
      return values().single { it.value == value }
    }
  }
}
