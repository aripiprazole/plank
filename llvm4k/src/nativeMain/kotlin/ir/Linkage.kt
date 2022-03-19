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

import llvm.LLVMLinkage

/**
 * An enumeration for the kinds of linkage for global values.
 */
public actual enum class Linkage(public val llvm: LLVMLinkage) {
  /**  Externally visible function */
  External(LLVMLinkage.LLVMExternalLinkage),

  /** Available for inspection, not emission. */
  AvailableExternally(LLVMLinkage.LLVMAvailableExternallyLinkage),

  /** Keep one copy of function when linking (inline) */
  LinkOnceAny(LLVMLinkage.LLVMLinkOnceAnyLinkage),

  /** Same, but only replaced by something equivalent. */
  LinkOnceODR(LLVMLinkage.LLVMLinkOnceODRLinkage),

  /** Keep one copy of named function when linking (weak) */
  WeakAny(LLVMLinkage.LLVMWeakAnyLinkage),

  /** Same, but only replaced by something equivalent. */
  WeakODR(LLVMLinkage.LLVMWeakODRLinkage),

  /** Special purpose, only applies to global arrays */
  Appending(LLVMLinkage.LLVMAppendingLinkage),

  /** Rename collisions when linking (static functions). */
  Internal(LLVMLinkage.LLVMInternalLinkage),

  /** Like Internal, but omit from symbol table. */
  Private(LLVMLinkage.LLVMPrivateLinkage),

  /** ExternalWeak linkage description. */
  ExternalWeak(LLVMLinkage.LLVMExternalLinkage),

  /** Tentative definitions. */
  Common(LLVMLinkage.LLVMCommonLinkage);

  public actual val value: UInt get() = llvm.value

  public actual companion object {
    public fun byValue(llvm: LLVMLinkage): Linkage {
      return byValue(llvm.value)
    }

    public actual fun byValue(value: Int): Linkage {
      return byValue(value.toUInt())
    }

    public actual fun byValue(value: UInt): Linkage {
      return values().single { it.value == value }
    }
  }
}
