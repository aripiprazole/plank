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
import llvm.LLVMValueRef
import org.plank.llvm4k.Module

public actual sealed class GlobalValue : Constant(), NamedValue {
  public actual open var linkage: Linkage
    get(): Linkage = Linkage.byValue(llvm.LLVMGetLinkage(ref).value)
    set(value) {
      llvm.LLVMSetLinkage(ref, value.llvm)
    }

  public actual open var visibility: Visibility
    get(): Visibility = Visibility.byValue(llvm.LLVMGetVisibility(ref).value)
    set(value) {
      llvm.LLVMSetVisibility(ref, value.llvm)
    }

  public actual open var section: String?
    get(): String? = llvm.LLVMGetSection(ref)?.toKString()
    set(value) {
      llvm.LLVMSetSection(ref, value)
    }

  public actual open var dllStorageClass: DLLStorageClass
    get(): DLLStorageClass = DLLStorageClass.byValue(llvm.LLVMGetDLLStorageClass(ref))
    set(value) {
      llvm.LLVMSetDLLStorageClass(ref, value.llvm)
    }

  public actual open var unnamedAddr: UnnamedAddr
    get(): UnnamedAddr = UnnamedAddr.byValue(llvm.LLVMGetUnnamedAddress(ref))
    set(value) {
      llvm.LLVMSetUnnamedAddress(ref, value.llvm)
    }

  public actual open val module: Module get() = Module(llvm.LLVMGetGlobalParent(ref))
  public actual open val valueType: Type get() = Type(llvm.LLVMGlobalGetValueType(ref))
}

public actual class GlobalAlias(public override val ref: LLVMValueRef?) : GlobalValue() {
  public actual var aliasee: Constant
    get(): Constant = Value(llvm.LLVMAliasGetAliasee(ref)) as? Constant
      ?: error("Aliasee expected to be a Constant")
    set(value) {
      llvm.LLVMAliasSetAliasee(ref, value.ref)
    }
}
