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

import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.global.LLVM
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.Value

internal fun Type.printToString(): String {
  return LLVM.LLVMPrintTypeToString(ref)!!.getString(Charsets.UTF_8)
}

internal fun Value.printToString(): String {
  return LLVM.LLVMPrintValueToString(ref)!!.getString(Charsets.UTF_8)
}

internal fun Boolean.toInt(): Int = if (this) 1 else 0

internal inline fun <reified T : Pointer> Collection<T?>.toPointerPointer(): PointerPointer<T> {
  return PointerPointer(*toTypedArray())
}
