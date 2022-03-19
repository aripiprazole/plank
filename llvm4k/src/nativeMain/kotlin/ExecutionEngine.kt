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

import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCStringArray
import kotlinx.cinterop.toCValues
import llvm.LLVMExecutionEngineRef
import org.plank.llvm4k.ir.FloatType
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.IntegerType

public actual class ExecutionEngine(
  public override val ref: LLVMExecutionEngineRef?,
) : Disposable, Owner<LLVMExecutionEngineRef> {
  public actual fun runFunction(callee: Function, vararg args: GenericValue<*>): GenericValue<*> {
    val ref = llvm.LLVMRunFunction(
      ref, callee.ref, args.size.toUInt(), args.map { it.ref }.toCValues()
    )

    return when (val returnType = callee.returnType) {
      is FloatType -> FloatValue(returnType, ref)
      is IntegerType -> IntegerValue(true, returnType, ref)
      else -> AnyValue(returnType, ref)
    }
  }

  public actual fun runFunctionAsMain(callee: Function, args: Array<String>): Int = memScoped {
    llvm.LLVMRunFunctionAsMain(
      ref, callee.ref, args.size.toUInt(),
      args.toCStringArray(this),
      emptyArray<String>().toCStringArray(this)
    )
  }

  public override fun close() {
    llvm.LLVMDisposeExecutionEngine(ref)
  }

  public override fun toString(): String {
    return "ExecutionEngine"
  }
}
