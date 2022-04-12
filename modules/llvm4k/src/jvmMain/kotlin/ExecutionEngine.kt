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

@file:Suppress("USELESS_CAST")

package org.plank.llvm4k

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.llvm.LLVM.LLVMExecutionEngineRef
import org.bytedeco.llvm.LLVM.LLVMGenericValueRef
import org.bytedeco.llvm.global.LLVM
import org.plank.llvm4k.ir.FloatType
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.IntegerType

public actual class ExecutionEngine(
  public override val ref: LLVMExecutionEngineRef?,
) : Disposable, Owner<LLVMExecutionEngineRef> {
  public actual fun runFunction(callee: Function, vararg args: GenericValue<*>): GenericValue<*> {
    val ref = LLVM.LLVMRunFunction(
      ref, callee.ref, args.size, args.map { it.ref }.toPointerPointer()
    )

    return when (val returnType = callee.returnType) {
      is FloatType -> FloatValue(returnType, ref)
      is IntegerType -> IntegerValue(true, returnType, ref)
      else -> AnyValue(returnType, ref as LLVMGenericValueRef?)
    }
  }

  public actual fun runFunctionAsMain(callee: Function, args: Array<String>): Int {
    return LLVM.LLVMRunFunctionAsMain(
      ref, callee.ref, args.size,
      args.map { BytePointer(it) }.toPointerPointer(),
      emptyArray<String>().map { BytePointer(it) }.toPointerPointer(),
    )
  }

  public override fun close() {
    LLVM.LLVMDisposeExecutionEngine(ref)
  }

  public override fun toString(): String {
    return "ExecutionEngine"
  }
}
