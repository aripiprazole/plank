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

/**
 * An enumeration for the kinds of linkage for global values.
 */
public expect enum class Linkage {
  /**  Externally visible function */
  External,

  /** Available for inspection, not emission. */
  AvailableExternally,

  /** Keep one copy of function when linking (inline) */
  LinkOnceAny,

  /** Same, but only replaced by something equivalent. */
  LinkOnceODR,

  /** Keep one copy of named function when linking (weak) */
  WeakAny,

  /** Same, but only replaced by something equivalent. */
  WeakODR,

  /** Special purpose, only applies to global arrays */
  Appending,

  /** Rename collisions when linking (static functions). */
  Internal,

  /** Like Internal, but omit from symbol table. */
  Private,

  /** ExternalWeak linkage description. */
  ExternalWeak,

  /** Tentative definitions. */
  Common;

  public val value: UInt

  public companion object {
    public fun byValue(value: Int): Linkage

    public fun byValue(value: UInt): Linkage
  }
}
