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

import kotlinx.cinterop.toKString
import llvm.LLVMValueKind
import llvm.LLVMValueKind.LLVMArgumentValueKind
import llvm.LLVMValueKind.LLVMBasicBlockValueKind
import llvm.LLVMValueKind.LLVMBlockAddressValueKind
import llvm.LLVMValueKind.LLVMConstantAggregateZeroValueKind
import llvm.LLVMValueKind.LLVMConstantArrayValueKind
import llvm.LLVMValueKind.LLVMConstantDataArrayValueKind
import llvm.LLVMValueKind.LLVMConstantDataVectorValueKind
import llvm.LLVMValueKind.LLVMConstantExprValueKind
import llvm.LLVMValueKind.LLVMConstantFPValueKind
import llvm.LLVMValueKind.LLVMConstantIntValueKind
import llvm.LLVMValueKind.LLVMConstantPointerNullValueKind
import llvm.LLVMValueKind.LLVMConstantStructValueKind
import llvm.LLVMValueKind.LLVMConstantTokenNoneValueKind
import llvm.LLVMValueKind.LLVMConstantVectorValueKind
import llvm.LLVMValueKind.LLVMFunctionValueKind
import llvm.LLVMValueKind.LLVMGlobalAliasValueKind
import llvm.LLVMValueKind.LLVMGlobalIFuncValueKind
import llvm.LLVMValueKind.LLVMGlobalVariableValueKind
import llvm.LLVMValueKind.LLVMInlineAsmValueKind
import llvm.LLVMValueKind.LLVMInstructionValueKind
import llvm.LLVMValueKind.LLVMMemoryDefValueKind
import llvm.LLVMValueKind.LLVMMemoryPhiValueKind
import llvm.LLVMValueKind.LLVMMemoryUseValueKind
import llvm.LLVMValueKind.LLVMMetadataAsValueValueKind
import llvm.LLVMValueKind.LLVMPoisonValueValueKind
import llvm.LLVMValueKind.LLVMUndefValueValueKind
import llvm.LLVMValueRef
import org.plank.llvm4k.Owner
import org.plank.llvm4k.printToString

public actual sealed class Value : Owner<LLVMValueRef> {
  public actual open val type: Type get() = Type(llvm.LLVMTypeOf(ref))
  public actual open val kind: Kind get() = Kind.byValue(llvm.LLVMGetValueKind(ref).value)

  public actual open val isConstant: Boolean get() = llvm.LLVMIsConstant(ref) == 1
  public actual open val isUndef: Boolean get() = llvm.LLVMIsUndef(ref) == 1
  public actual open val asBasicBlock: BasicBlock get() = BasicBlock(llvm.LLVMValueAsBasicBlock(ref))

  public actual open fun replace(other: Value) {
    llvm.LLVMReplaceAllUsesWith(ref, other.ref)
  }

  public actual override fun toString(): String = printToString()

  public actual enum class Kind(public val llvm: LLVMValueKind) {
    Argument(LLVMArgumentValueKind),
    BasicBlock(LLVMBasicBlockValueKind),
    MemoryUse(LLVMMemoryUseValueKind),
    MemoryDef(LLVMMemoryDefValueKind),
    MemoryPhi(LLVMMemoryPhiValueKind),
    Function(LLVMFunctionValueKind),
    GlobalAlias(LLVMGlobalAliasValueKind),
    GlobalIFunc(LLVMGlobalIFuncValueKind),
    GlobalVariable(LLVMGlobalVariableValueKind),
    BlockAddress(LLVMBlockAddressValueKind),
    ConstantExpr(LLVMConstantExprValueKind),
    ConstantArray(LLVMConstantArrayValueKind),
    ConstantStruct(LLVMConstantStructValueKind),
    ConstantVector(LLVMConstantVectorValueKind),
    UndefValue(LLVMUndefValueValueKind),
    ConstantAggregateZero(LLVMConstantAggregateZeroValueKind),
    ConstantDataArray(LLVMConstantDataArrayValueKind),
    ConstantDataVector(LLVMConstantDataVectorValueKind),
    ConstantInt(LLVMConstantIntValueKind),
    ConstantFP(LLVMConstantFPValueKind),
    ConstantPointerNull(LLVMConstantPointerNullValueKind),
    ConstantTokenNone(LLVMConstantTokenNoneValueKind),
    MetadataAsValue(LLVMMetadataAsValueValueKind),
    InlineAsm(LLVMInlineAsmValueKind),
    Instruction(LLVMInstructionValueKind),
    PoisonValue(LLVMPoisonValueValueKind);

    public actual val value: UInt get() = llvm.value

    public actual companion object {
      public fun byValue(llvm: LLVMValueKind): Kind {
        return byValue(llvm.value)
      }

      public actual fun byValue(value: Int): Kind {
        return byValue(value.toUInt())
      }

      public actual fun byValue(value: UInt): Kind {
        return values().single { it.value == value }
      }
    }
  }
}

public actual interface NamedValue : Owner<LLVMValueRef> {
  public actual var name: String
    get(): String = llvm.LLVMGetValueName(ref)!!.toKString()
    set(value) {
      llvm.LLVMSetValueName(ref, value)
    }
}

@Suppress("ComplexMethod")
public fun Value(ref: LLVMValueRef?): Value {
  return when (val kind = llvm.LLVMGetValueKind(ref)) {
    LLVMArgumentValueKind -> Argument(ref)
    LLVMBasicBlockValueKind -> BasicBlock.AsValue(ref)
    LLVMMemoryUseValueKind -> MemoryUse(ref)
    LLVMMemoryDefValueKind -> MemoryDef(ref)
    LLVMMemoryPhiValueKind -> MemoryPhi(ref)
    LLVMFunctionValueKind -> Function(ref)
    LLVMGlobalAliasValueKind -> GlobalAlias(ref)
    LLVMGlobalIFuncValueKind -> GlobalIFunc(ref)
    LLVMGlobalVariableValueKind -> GlobalVariable(ref)
    LLVMBlockAddressValueKind -> BlockAddress(ref)
    LLVMConstantExprValueKind -> ConstantExpr(ref)
    LLVMConstantArrayValueKind -> ConstantDataArray(ref)
    LLVMConstantStructValueKind -> ConstantAggregate(ref)
    LLVMConstantVectorValueKind -> ConstantDataVector(ref)
    LLVMUndefValueValueKind -> UndefValue(ref)
    LLVMConstantAggregateZeroValueKind -> ConstantAggregate(ref)
    LLVMConstantDataArrayValueKind -> ConstantDataArray(ref)
    LLVMConstantDataVectorValueKind -> ConstantDataVector(ref)
    LLVMConstantIntValueKind -> ConstantInt(ref)
    LLVMConstantFPValueKind -> ConstantFP(ref)
    LLVMConstantPointerNullValueKind -> ConstantPointerNull(ref)
    LLVMConstantTokenNoneValueKind -> ConstantTokenNone(ref)
    LLVMMetadataAsValueValueKind -> MetadataAsValue(ref)
    LLVMInlineAsmValueKind -> InlineAsm(ref)
    LLVMInstructionValueKind -> Instruction(ref)
    LLVMPoisonValueValueKind -> PoisonValue(ref)
    else -> error("Unknown value kind: $kind")
  }
}
