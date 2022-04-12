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

import llvm.LLVMAtomicRMWBinOp

public actual enum class AtomicRMWBinOp(public val llvm: LLVMAtomicRMWBinOp) {
  Xchg(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpXchg),
  Add(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpAdd),
  Sub(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpSub),
  And(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpAnd),
  Nand(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpNand),
  Or(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpOr),
  Xor(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpXor),
  Max(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpMax),
  Min(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpMin),
  UMax(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpUMax),
  UMin(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpUMin),
  FAdd(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpFAdd),
  FSub(LLVMAtomicRMWBinOp.LLVMAtomicRMWBinOpFSub);

  public actual val value: UInt get() = llvm.value

  public actual companion object {
    public fun byValue(llvm: LLVMAtomicRMWBinOp): AtomicRMWBinOp {
      return byValue(llvm.value)
    }

    public actual fun byValue(value: Int): AtomicRMWBinOp {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): AtomicRMWBinOp {
      return values().single { it.value == value }
    }
  }
}
