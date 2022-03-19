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

import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.toCValues
import llvm.LLVMOpcode.LLVMAlloca
import llvm.LLVMOpcode.LLVMAtomicCmpXchg
import llvm.LLVMOpcode.LLVMAtomicRMW
import llvm.LLVMOpcode.LLVMBr
import llvm.LLVMOpcode.LLVMCall
import llvm.LLVMOpcode.LLVMCatchPad
import llvm.LLVMOpcode.LLVMCatchRet
import llvm.LLVMOpcode.LLVMCatchSwitch
import llvm.LLVMOpcode.LLVMCleanupPad
import llvm.LLVMOpcode.LLVMFence
import llvm.LLVMOpcode.LLVMIndirectBr
import llvm.LLVMOpcode.LLVMInvoke
import llvm.LLVMOpcode.LLVMLandingPad
import llvm.LLVMOpcode.LLVMLoad
import llvm.LLVMOpcode.LLVMPHI
import llvm.LLVMOpcode.LLVMResume
import llvm.LLVMOpcode.LLVMRet
import llvm.LLVMOpcode.LLVMStore
import llvm.LLVMOpcode.LLVMSwitch
import llvm.LLVMOpcode.LLVMUnreachable
import llvm.LLVMValueRef

public actual sealed class Instruction : User()

public actual class CallInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class ReturnInst(public override val ref: LLVMValueRef?) : Instruction()

public actual class BranchInst(public override val ref: LLVMValueRef?) : Instruction() {
  public actual val isConditional: Boolean get() = llvm.LLVMIsConditional(ref) == 1

  public actual var condition: Value?
    get(): Value? = when (isConditional) {
      true -> Value(llvm.LLVMGetCondition(ref))
      false -> null
    }
    set(value) {
      if (!isConditional) return
      if (value == null) return

      llvm.LLVMSetCondition(ref, value.ref)
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
    llvm.LLVMAddIncoming(ref, cValuesOf(value.ref), cValuesOf(block.ref), 1u)
  }

  public actual fun addIncoming(vararg incoming: Pair<Value, BasicBlock>) {
    llvm.LLVMAddIncoming(
      ref,
      incoming.map { it.first.ref }.toCValues(),
      incoming.map { it.second.ref }.toCValues(),
      incoming.size.toUInt(),
    )
  }
}

@Suppress("ComplexMethod", "LongMethod")
public fun Instruction(ref: LLVMValueRef?): Instruction {
  return when (llvm.LLVMGetInstructionOpcode(ref)) {
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
