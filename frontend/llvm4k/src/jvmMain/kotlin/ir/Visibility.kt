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

import org.bytedeco.llvm.global.LLVM.LLVMDefaultVisibility
import org.bytedeco.llvm.global.LLVM.LLVMHiddenVisibility
import org.bytedeco.llvm.global.LLVM.LLVMProtectedVisibility

/** An enumeration for the kinds of visibility of global values. */
public actual enum class Visibility(public val llvm: Int) {
  /** The GV is visible. */
  Default(LLVMDefaultVisibility),

  /** The GV is hidden. */
  Hidden(LLVMHiddenVisibility),

  /** The GV is protected. */
  Protected(LLVMProtectedVisibility);

  public actual val value: UInt get() = llvm.toUInt()

  public actual companion object {
    public actual fun byValue(value: Int): Visibility {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): Visibility {
      return values().single { it.value == value }
    }
  }
}
