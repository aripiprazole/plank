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

import org.bytedeco.llvm.global.LLVM.LLVMIntEQ
import org.bytedeco.llvm.global.LLVM.LLVMIntNE
import org.bytedeco.llvm.global.LLVM.LLVMIntSGE
import org.bytedeco.llvm.global.LLVM.LLVMIntSGT
import org.bytedeco.llvm.global.LLVM.LLVMIntSLE
import org.bytedeco.llvm.global.LLVM.LLVMIntSLT
import org.bytedeco.llvm.global.LLVM.LLVMIntUGE
import org.bytedeco.llvm.global.LLVM.LLVMIntUGT
import org.bytedeco.llvm.global.LLVM.LLVMIntULE
import org.bytedeco.llvm.global.LLVM.LLVMIntULT

public actual enum class IntPredicate(public val llvm: Int) {
  EQ(LLVMIntEQ),
  NE(LLVMIntNE),
  UGT(LLVMIntUGT),
  UGE(LLVMIntUGE),
  ULT(LLVMIntULT),
  ULE(LLVMIntULE),
  SGT(LLVMIntSGT),
  SGE(LLVMIntSGE),
  SLT(LLVMIntSLT),
  SLE(LLVMIntSLE);

  public actual val value: UInt get() = llvm.toUInt()

  public actual companion object {
    public actual fun byValue(value: Int): IntPredicate {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): IntPredicate {
      return values().single { it.value == value }
    }
  }
}
