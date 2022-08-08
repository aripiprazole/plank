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

import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpAdd
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpAnd
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpFAdd
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpFSub
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpMax
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpMin
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpNand
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpOr
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpSub
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpUMax
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpUMin
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpXchg
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMWBinOpXor

public actual enum class AtomicRMWBinOp(public val llvm: Int) {
  Xchg(LLVMAtomicRMWBinOpXchg),
  Add(LLVMAtomicRMWBinOpAdd),
  Sub(LLVMAtomicRMWBinOpSub),
  And(LLVMAtomicRMWBinOpAnd),
  Nand(LLVMAtomicRMWBinOpNand),
  Or(LLVMAtomicRMWBinOpOr),
  Xor(LLVMAtomicRMWBinOpXor),
  Max(LLVMAtomicRMWBinOpMax),
  Min(LLVMAtomicRMWBinOpMin),
  UMax(LLVMAtomicRMWBinOpUMax),
  UMin(LLVMAtomicRMWBinOpUMin),
  FAdd(LLVMAtomicRMWBinOpFAdd),
  FSub(LLVMAtomicRMWBinOpFSub);

  public actual val value: UInt get() = llvm.toUInt()

  public actual companion object {
    public actual fun byValue(value: Int): AtomicRMWBinOp {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): AtomicRMWBinOp {
      return values().single { it.value == value }
    }
  }
}
