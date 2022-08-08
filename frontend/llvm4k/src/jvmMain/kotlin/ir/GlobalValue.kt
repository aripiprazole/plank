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
import org.plank.llvm4k.Module

public actual sealed class GlobalValue : Constant(), NamedValue {
  public actual open var linkage: Linkage
    get(): Linkage = Linkage.byValue(LLVM.LLVMGetLinkage(ref))
    set(value) {
      LLVM.LLVMSetLinkage(ref, value.llvm)
    }

  public actual open var visibility: Visibility
    get(): Visibility = Visibility.byValue(LLVM.LLVMGetVisibility(ref))
    set(value) {
      LLVM.LLVMSetVisibility(ref, value.llvm)
    }

  public actual open var section: String?
    get(): String? = LLVM.LLVMGetSection(ref)?.getString(Charsets.UTF_8)
    set(value) {
      LLVM.LLVMSetSection(ref, value)
    }

  public actual open var dllStorageClass: DLLStorageClass
    get(): DLLStorageClass = DLLStorageClass.byValue(LLVM.LLVMGetDLLStorageClass(ref))
    set(value) {
      LLVM.LLVMSetDLLStorageClass(ref, value.llvm)
    }

  public actual open var unnamedAddr: UnnamedAddr
    get(): UnnamedAddr = UnnamedAddr.byValue(LLVM.LLVMGetUnnamedAddress(ref))
    set(value) {
      LLVM.LLVMSetUnnamedAddress(ref, value.llvm)
    }

  public actual open val module: Module get() = Module(LLVM.LLVMGetGlobalParent(ref))
  public actual open val valueType: Type get() = Type(LLVM.LLVMGlobalGetValueType(ref))
}

public actual class GlobalAlias(public override val ref: LLVMValueRef?) : GlobalValue() {
  public actual var aliasee: Constant
    get(): Constant = Value(LLVM.LLVMAliasGetAliasee(ref)) as? Constant
      ?: error("Aliasee expected to be a Constant")
    set(value) {
      LLVM.LLVMAliasSetAliasee(ref, value.ref)
    }
}
