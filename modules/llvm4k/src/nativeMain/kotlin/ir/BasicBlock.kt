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
import llvm.LLVMBasicBlockRef
import llvm.LLVMValueRef
import org.plank.llvm4k.Owner

public actual class BasicBlock(public override val ref: LLVMBasicBlockRef?) :
  Owner<LLVMBasicBlockRef> {
  public actual val name: String get() = llvm.LLVMGetBasicBlockName(ref)!!.toKString()
  public actual val function: Function? get() = llvm.LLVMGetBasicBlockParent(ref)?.let(::Function)

  public actual val asValue: AsValue get() = AsValue(llvm.LLVMBasicBlockAsValue(ref))

  public actual fun moveAfter(target: BasicBlock) {
    llvm.LLVMMoveBasicBlockAfter(ref, target.ref)
  }

  public actual fun moveBefore(target: BasicBlock) {
    llvm.LLVMMoveBasicBlockBefore(ref, target.ref)
  }

  public actual fun delete() {
    llvm.LLVMDeleteBasicBlock(ref)
  }

  public actual fun erase() {
    llvm.LLVMRemoveBasicBlockFromParent(ref)
  }

  public actual override fun toString(): String = asValue.toString()

  public actual class AsValue(public override val ref: LLVMValueRef?) : Value()
}
