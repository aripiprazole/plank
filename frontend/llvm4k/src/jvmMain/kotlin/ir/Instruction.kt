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

import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import org.bytedeco.llvm.global.LLVM.LLVMAlloca
import org.bytedeco.llvm.global.LLVM.LLVMAtomicCmpXchg
import org.bytedeco.llvm.global.LLVM.LLVMAtomicRMW
import org.bytedeco.llvm.global.LLVM.LLVMBr
import org.bytedeco.llvm.global.LLVM.LLVMCall
import org.bytedeco.llvm.global.LLVM.LLVMCatchPad
import org.bytedeco.llvm.global.LLVM.LLVMCatchRet
import org.bytedeco.llvm.global.LLVM.LLVMCatchSwitch
import org.bytedeco.llvm.global.LLVM.LLVMCleanupPad
import org.bytedeco.llvm.global.LLVM.LLVMFence
import org.bytedeco.llvm.global.LLVM.LLVMIndirectBr
import org.bytedeco.llvm.global.LLVM.LLVMInvoke
import org.bytedeco.llvm.global.LLVM.LLVMLandingPad
import org.bytedeco.llvm.global.LLVM.LLVMLoad
import org.bytedeco.llvm.global.LLVM.LLVMPHI
import org.bytedeco.llvm.global.LLVM.LLVMResume
import org.bytedeco.llvm.global.LLVM.LLVMRet
import org.bytedeco.llvm.global.LLVM.LLVMStore
import org.bytedeco.llvm.global.LLVM.LLVMSwitch
import org.bytedeco.llvm.global.LLVM.LLVMUnreachable
import org.plank.llvm4k.toPointerPointer

public actual sealed class Instruction : User()

public actual class CallInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class ReturnInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class BranchInst(public override val ref: LLVMValueRef?) : Instruction() {
  public actual val isConditional: Boolean get() = LLVM.LLVMIsConditional(ref) == 1

  public actual var condition: Value?
    get(): Value? = when (isConditional) {
      true -> Value(LLVM.LLVMGetCondition(ref))
      false -> null
    }
    set(value) {
      if (!isConditional) return
      if (value == null) return

      LLVM.LLVMSetCondition(ref, value.ref)
    }
}

public actual class SwitchInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class IndirectBrInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class InvokeInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class ResumeInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class CleanupReturnInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class CatchSwitchInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class CatchPadInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class CleanupPadInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class CatchReturnInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class UnreachableInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class AllocaInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class LoadInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class StoreInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class FenceInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class AtomicCmpXchgInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class AtomicRMWInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class LandingPadInst(public override val ref: LLVMValueRef?) : Instruction()

private class InstructionImpl(override val ref: LLVMValueRef?) : Instruction()

public actual class PhiInst(public override val ref: LLVMValueRef?) : Instruction() {
  public actual fun addIncoming(value: Value, block: BasicBlock) {
    LLVM.LLVMAddIncoming(ref, value.ref, block.ref, 1)
  }

  public actual fun addIncoming(vararg incoming: Pair<Value, BasicBlock>) {
    LLVM.LLVMAddIncoming(
      ref,
      incoming.map { it.first.ref }.toPointerPointer(),
      incoming.map { it.second.ref }.toPointerPointer(),
      incoming.size,
    )
  }
}

@Suppress("ComplexMethod", "LongMethod")
public fun Instruction(ref: LLVMValueRef?): Instruction {
  return when (LLVM.LLVMGetInstructionOpcode(ref)) {
    LLVMRet -> ReturnInst(ref)
    LLVMBr -> BranchInst(ref)
    LLVMSwitch -> SwitchInst(ref)
    LLVMIndirectBr -> IndirectBrInst(ref)
    LLVMInvoke -> InvokeInst(ref)
    LLVMUnreachable -> UnreachableInst(ref)
    LLVMAlloca -> AllocaInst(ref)
    LLVMLoad -> LoadInst(ref)
    LLVMStore -> StoreInst(ref)
    LLVMCall -> CallInst(ref)
    LLVMFence -> FenceInst(ref)
    LLVMAtomicCmpXchg -> AtomicCmpXchgInst(ref)
    LLVMAtomicRMW -> AtomicRMWInst(ref)
    LLVMResume -> ResumeInst(ref)
    LLVMLandingPad -> LandingPadInst(ref)
    LLVMCatchRet -> CatchReturnInst(ref)
    LLVMCatchPad -> CatchPadInst(ref)
    LLVMCleanupPad -> CleanupPadInst(ref)
    LLVMCatchSwitch -> CatchSwitchInst(ref)
    LLVMPHI -> PhiInst(ref)
    else -> InstructionImpl(ref)
  }
}
