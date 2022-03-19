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

import llvm.LLVMRealPredicate

public actual enum class RealPredicate(public val llvm: LLVMRealPredicate) {
  False(LLVMRealPredicate.LLVMRealPredicateFalse),
  OEQ(LLVMRealPredicate.LLVMRealOEQ),
  OGT(LLVMRealPredicate.LLVMRealOGT),
  OGE(LLVMRealPredicate.LLVMRealOGE),
  OLT(LLVMRealPredicate.LLVMRealOLT),
  OLE(LLVMRealPredicate.LLVMRealOLE),
  ONE(LLVMRealPredicate.LLVMRealONE),
  ORD(LLVMRealPredicate.LLVMRealORD),
  UNO(LLVMRealPredicate.LLVMRealUNO),
  UEQ(LLVMRealPredicate.LLVMRealUEQ),
  UGT(LLVMRealPredicate.LLVMRealUGT),
  UGE(LLVMRealPredicate.LLVMRealUGE),
  ULT(LLVMRealPredicate.LLVMRealULT),
  ULE(LLVMRealPredicate.LLVMRealULE),
  UNE(LLVMRealPredicate.LLVMRealUNE),
  True(LLVMRealPredicate.LLVMRealPredicateTrue);

  public actual val value: UInt get() = llvm.value

  public actual companion object {
    public fun byValue(llvm: LLVMRealPredicate): RealPredicate {
      return byValue(llvm.value)
    }

    public actual fun byValue(value: Int): RealPredicate {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): RealPredicate {
      return values().single { it.value == value }
    }
  }
}
