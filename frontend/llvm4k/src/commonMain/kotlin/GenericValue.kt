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

import org.plank.llvm4k.ir.IntegerType
import org.plank.llvm4k.ir.Type

public expect sealed interface GenericValue<A> : Disposable {
  public val type: Type
  public val value: A
}

public expect class AnyValue(type: Type, value: Any) : GenericValue<Any?>

public expect class FloatValue(type: Type, value: Int) : GenericValue<Float>

public expect class IntegerValue(type: IntegerType, value: Int, signed: Boolean = true) :
  GenericValue<Int> {
  public val signed: Boolean
}

public inline fun <reified B> GenericValue<*>.cast(): B {
  if (value !is B) throw LLVMError("Cannot cast ${value!!::class} to ${B::class.simpleName}")

  return value as B
}
