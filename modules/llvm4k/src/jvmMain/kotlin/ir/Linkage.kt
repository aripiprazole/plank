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

import org.bytedeco.llvm.global.LLVM.LLVMAppendingLinkage
import org.bytedeco.llvm.global.LLVM.LLVMAvailableExternallyLinkage
import org.bytedeco.llvm.global.LLVM.LLVMCommonLinkage
import org.bytedeco.llvm.global.LLVM.LLVMExternalLinkage
import org.bytedeco.llvm.global.LLVM.LLVMInternalLinkage
import org.bytedeco.llvm.global.LLVM.LLVMLinkOnceAnyLinkage
import org.bytedeco.llvm.global.LLVM.LLVMLinkOnceODRLinkage
import org.bytedeco.llvm.global.LLVM.LLVMPrivateLinkage
import org.bytedeco.llvm.global.LLVM.LLVMWeakAnyLinkage
import org.bytedeco.llvm.global.LLVM.LLVMWeakODRLinkage

/**
 * An enumeration for the kinds of linkage for global values.
 */
public actual enum class Linkage(public val llvm: Int) {
  /**  Externally visible function */
  External(LLVMExternalLinkage),

  /** Available for inspection, not emission. */
  AvailableExternally(LLVMAvailableExternallyLinkage),

  /** Keep one copy of function when linking (inline) */
  LinkOnceAny(LLVMLinkOnceAnyLinkage),

  /** Same, but only replaced by something equivalent. */
  LinkOnceODR(LLVMLinkOnceODRLinkage),

  /** Keep one copy of named function when linking (weak) */
  WeakAny(LLVMWeakAnyLinkage),

  /** Same, but only replaced by something equivalent. */
  WeakODR(LLVMWeakODRLinkage),

  /** Special purpose, only applies to global arrays */
  Appending(LLVMAppendingLinkage),

  /** Rename collisions when linking (static functions). */
  Internal(LLVMInternalLinkage),

  /** Like Internal, but omit from symbol table. */
  Private(LLVMPrivateLinkage),

  /** ExternalWeak linkage description. */
  ExternalWeak(LLVMExternalLinkage),

  /** Tentative definitions. */
  Common(LLVMCommonLinkage);

  public actual val value: UInt get() = llvm.toUInt()

  public actual companion object {
    public actual fun byValue(value: Int): Linkage {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): Linkage {
      return values().single { it.value == value }
    }
  }
}
