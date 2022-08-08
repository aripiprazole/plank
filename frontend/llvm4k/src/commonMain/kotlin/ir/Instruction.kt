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

public expect sealed class Instruction : User

public expect class CallInst : Instruction

public expect class ReturnInst : Instruction

public expect class BranchInst : Instruction {
  public val isConditional: Boolean

  public var condition: Value?
}

public expect class SwitchInst : Instruction

public expect class IndirectBrInst : Instruction

public expect class InvokeInst : Instruction

public expect class ResumeInst : Instruction

public expect class CleanupReturnInst : Instruction

public expect class CatchSwitchInst : Instruction

public expect class CatchPadInst : Instruction

public expect class CleanupPadInst : Instruction

public expect class CatchReturnInst : Instruction

public expect class UnreachableInst : Instruction

public expect class AllocaInst : Instruction

public expect class LoadInst : Instruction

public expect class StoreInst : Instruction

public expect class FenceInst : Instruction

public expect class AtomicCmpXchgInst : Instruction

public expect class AtomicRMWInst : Instruction

public expect class LandingPadInst : Instruction

public expect class PhiInst : Instruction {
  public fun addIncoming(value: Value, block: BasicBlock)

  public fun addIncoming(vararg incoming: Pair<Value, BasicBlock>)
}
