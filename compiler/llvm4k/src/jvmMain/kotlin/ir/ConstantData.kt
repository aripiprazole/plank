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

import org.bytedeco.javacpp.IntPointer
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM

public actual sealed class ConstantData : Constant()

public actual class ConstantAggregate(public override val ref: LLVMValueRef?) : ConstantData()

public actual sealed class ConstantDataSequential : ConstantData()

public actual class ConstantDataArray(public override val ref: LLVMValueRef?) :
  ConstantDataSequential()

public actual class ConstantDataVector(public override val ref: LLVMValueRef?) :
  ConstantDataSequential()

public actual class ConstantFP(public override val ref: LLVMValueRef?) : ConstantData() {
  public actual val realValue: FPValue
    get(): FPValue {
      val lossy = IntPointer(1L)
      val value = LLVM.LLVMConstRealGetDouble(ref, lossy).toFloat()

      return FPValue(value, lossy.get() == 1)
    }

  public actual val value: Float get() = realValue.value
}

public actual class ConstantInt(public override val ref: LLVMValueRef?) : ConstantData() {
  public actual val zExtValue: Long get() = LLVM.LLVMConstIntGetZExtValue(ref).toLong()
  public actual val sExtValue: Long get() = LLVM.LLVMConstIntGetSExtValue(ref)
}

public actual class ConstantPointerNull(public override val ref: LLVMValueRef?) : ConstantData()

public actual class ConstantTokenNone(public override val ref: LLVMValueRef?) : ConstantData()

public actual sealed class UndefValue : ConstantData()

private class UndefValueImpl(override val ref: LLVMValueRef?) : UndefValue()

public actual class PoisonValue(public override val ref: LLVMValueRef?) : UndefValue()

public fun UndefValue(ref: LLVMValueRef?): UndefValue {
  return UndefValueImpl(ref)
}
