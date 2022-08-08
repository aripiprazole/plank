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
import org.bytedeco.llvm.global.LLVM.LLVMArgumentValueKind
import org.bytedeco.llvm.global.LLVM.LLVMBasicBlockValueKind
import org.bytedeco.llvm.global.LLVM.LLVMBlockAddressValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantAggregateZeroValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantArrayValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantDataArrayValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantDataVectorValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantExprValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantFPValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantIntValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantPointerNullValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantStructValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantTokenNoneValueKind
import org.bytedeco.llvm.global.LLVM.LLVMConstantVectorValueKind
import org.bytedeco.llvm.global.LLVM.LLVMFunctionValueKind
import org.bytedeco.llvm.global.LLVM.LLVMGlobalAliasValueKind
import org.bytedeco.llvm.global.LLVM.LLVMGlobalIFuncValueKind
import org.bytedeco.llvm.global.LLVM.LLVMGlobalVariableValueKind
import org.bytedeco.llvm.global.LLVM.LLVMInlineAsmValueKind
import org.bytedeco.llvm.global.LLVM.LLVMInstructionValueKind
import org.bytedeco.llvm.global.LLVM.LLVMMemoryDefValueKind
import org.bytedeco.llvm.global.LLVM.LLVMMemoryPhiValueKind
import org.bytedeco.llvm.global.LLVM.LLVMMemoryUseValueKind
import org.bytedeco.llvm.global.LLVM.LLVMMetadataAsValueValueKind
import org.bytedeco.llvm.global.LLVM.LLVMPoisonValueValueKind
import org.bytedeco.llvm.global.LLVM.LLVMUndefValueValueKind
import org.plank.llvm4k.Owner
import org.plank.llvm4k.printToString

public actual sealed class Value : Owner<LLVMValueRef> {
  public actual open val type: Type get() = Type(LLVM.LLVMTypeOf(ref))
  public actual open val kind: Kind get() = Kind.byValue(LLVM.LLVMGetValueKind(ref))

  public actual open val isConstant: Boolean get() = LLVM.LLVMIsConstant(ref) == 1
  public actual open val isUndef: Boolean get() = LLVM.LLVMIsUndef(ref) == 1
  public actual open val asBasicBlock: BasicBlock
    get() = BasicBlock(LLVM.LLVMValueAsBasicBlock(ref))

  public actual open fun replace(other: Value) {
    LLVM.LLVMReplaceAllUsesWith(ref, other.ref)
  }

  public actual override fun toString(): String = printToString()

  public actual enum class Kind(public val llvm: Int) {
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

    public actual val value: UInt get() = llvm.toUInt()

    public actual companion object {
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
    get(): String = LLVM.LLVMGetValueName(ref)!!.getString(Charsets.UTF_8)
    set(value) {
      LLVM.LLVMSetValueName(ref, value)
    }
}

@Suppress("ComplexMethod")
public fun Value(ref: LLVMValueRef?): Value {
  return when (val kind = LLVM.LLVMGetValueKind(ref)) {
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
