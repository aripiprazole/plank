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

package org.plank.llvm4k

import org.bytedeco.llvm.global.LLVM

/** Code generation optimization level. */
public actual enum class OptimizationLevel(public val llvm: Int) {
  /** -O0 */
  None(LLVM.LLVMCodeGenLevelNone),

  /** -O1 */
  Less(LLVM.LLVMCodeGenLevelLess),

  /** -O2 */
  Default(LLVM.LLVMCodeGenLevelDefault),

  /** -O3 */
  Aggressive(LLVM.LLVMCodeGenLevelAggressive);

  public actual val value: UInt get() = llvm.toUInt()

  public actual companion object {
    public actual fun byValue(value: Int): OptimizationLevel {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): OptimizationLevel {
      return values().single { it.value == value }
    }
  }
}
