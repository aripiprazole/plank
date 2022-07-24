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

import kotlinx.cinterop.StableRef
import llvm.LLVMGenericValueRef
import org.plank.llvm4k.ir.IntegerType
import org.plank.llvm4k.ir.Type

public actual sealed interface GenericValue<A> : Disposable, Owner<LLVMGenericValueRef> {
  public actual val type: Type
  public actual val value: A

  public override fun close() {
    llvm.LLVMDisposeGenericValue(ref)
  }
}

public actual class AnyValue(
  public override val type: Type,
  public override val ref: LLVMGenericValueRef?,
) : GenericValue<Any?> {
  public actual constructor(type: Type, value: Any) :
    this(type, llvm.LLVMCreateGenericValueOfPointer(StableRef.create(value).asCPointer()))

  public override val value: Any? get() = llvm.LLVMGenericValueToPointer(ref)

  public override fun toString(): String {
    return "AnyValue($value)"
  }
}

public actual class FloatValue(
  public override val type: Type,
  public override val ref: LLVMGenericValueRef?,
) : GenericValue<Float> {
  public actual constructor(type: Type, value: Int) :
    this(type, llvm.LLVMCreateGenericValueOfFloat(type.ref, value.toDouble()))

  public override val value: Float get() = llvm.LLVMGenericValueToFloat(type.ref, ref).toFloat()

  public override fun toString(): String {
    return "FloatValue($value)"
  }
}

public actual class IntegerValue(
  public actual val signed: Boolean,
  public override val type: IntegerType,
  public override val ref: LLVMGenericValueRef?,
) : GenericValue<Int> {
  public actual constructor(type: IntegerType, value: Int, signed: Boolean) :
    this(
      signed,
      type,
      llvm.LLVMCreateGenericValueOfInt(type.ref, value.toULong(), signed.toInt()),
    )

  public override val value: Int get() = llvm.LLVMGenericValueToInt(ref, signed.toInt()).toInt()

  public override fun toString(): String {
    return "IntegerValue($value)"
  }
}
