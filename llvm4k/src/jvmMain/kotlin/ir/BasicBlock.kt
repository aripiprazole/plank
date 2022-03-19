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

import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import org.plank.llvm4k.Owner

public actual class BasicBlock(public override val ref: LLVMBasicBlockRef?) :
  Owner<LLVMBasicBlockRef> {
  public actual val name: String get() = LLVM.LLVMGetBasicBlockName(ref).getString(Charsets.UTF_8)
  public actual val function: Function? get() = LLVM.LLVMGetBasicBlockParent(ref)?.let(::Function)

  public actual val asValue: AsValue get() = AsValue(LLVM.LLVMBasicBlockAsValue(ref))

  public actual fun moveAfter(target: BasicBlock) {
    LLVM.LLVMMoveBasicBlockAfter(ref, target.ref)
  }

  public actual fun moveBefore(target: BasicBlock) {
    LLVM.LLVMMoveBasicBlockBefore(ref, target.ref)
  }

  public actual fun delete() {
    LLVM.LLVMDeleteBasicBlock(ref)
  }

  public actual fun erase() {
    LLVM.LLVMRemoveBasicBlockFromParent(ref)
  }

  public actual override fun toString(): String = asValue.toString()

  public actual class AsValue(public override val ref: LLVMValueRef?) : Value()
}
