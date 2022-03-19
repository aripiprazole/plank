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

public sealed interface AddrSpace {
  public val value: UInt

  public object Generic : AddrSpace {
    public override val value: UInt = 0.toUInt()
  }

  public data class Other(public override val value: UInt) : AddrSpace

  public companion object {
    public fun byValue(value: UInt): AddrSpace = when (value) {
      0.toUInt() -> Generic
      else -> Other(value)
    }
  }
}
