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

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.SizeTPointer
import org.bytedeco.llvm.LLVM.LLVMExecutionEngineRef
import org.bytedeco.llvm.LLVM.LLVMModuleRef
import org.bytedeco.llvm.global.LLVM
import org.plank.llvm4k.ir.AddrSpace
import org.plank.llvm4k.ir.Constant
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.FunctionType
import org.plank.llvm4k.ir.GlobalAlias
import org.plank.llvm4k.ir.GlobalIFunc
import org.plank.llvm4k.ir.GlobalVariable
import org.plank.llvm4k.ir.PointerType
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.Type

public actual class Module(public override val ref: LLVMModuleRef?) :
  Disposable,
  Owner<LLVMModuleRef> {
  public actual val context: Context get() = Context(LLVM.LLVMGetModuleContext(ref))

  public actual var inlineAsm: String
    get(): String {
      val size = SizeTPointer()

      return LLVM.LLVMGetModuleInlineAsm(ref, size)!!.getString(Charsets.UTF_8)
    }
    set(value) {
      return LLVM.LLVMSetModuleInlineAsm2(ref, value, value.length.toLong())
    }

  public actual var sourceFilename: String
    get(): String {
      val size = SizeTPointer()

      return LLVM.LLVMGetSourceFileName(ref, size)!!.getString(Charsets.UTF_8)
    }
    set(value) {
      LLVM.LLVMSetSourceFileName(ref, value, value.length.toLong())
    }

  public actual var moduleIdentifier: String
    get(): String {
      val size = SizeTPointer()

      return LLVM.LLVMGetModuleIdentifier(ref, size)!!.getString(Charsets.UTF_8)
    }
    set(value) {
      LLVM.LLVMSetModuleIdentifier(ref, value, value.length.toLong())
    }

  public actual var dataLayout: String
    get(): String = LLVM.LLVMGetDataLayout(ref)!!.getString(Charsets.UTF_8)
    set(value) {
      LLVM.LLVMSetDataLayout(ref, value)
    }

  public actual fun dump(file: String) {
    val message = BytePointer()

    LLVM.LLVMPrintModuleToFile(ref, file, message)

    if (!message.isNull) {
      throw LLVMError(message.getString(Charsets.UTF_8))
    }
  }

  public actual fun appendInlineAsm(asm: String) {
    LLVM.LLVMAppendModuleInlineAsm(ref, asm, asm.length.toLong())
  }

  public actual fun writeBitcode(file: String) {
    LLVM.LLVMWriteBitcodeToFile(ref, file)
  }

  public actual fun getTypeByName(name: String): StructType? {
    return LLVM.LLVMGetTypeByName(ref, name)?.let(::StructType)
  }

  public actual fun createJITExecutionEngine(level: OptimizationLevel): ExecutionEngine {
    LLVM.LLVMInitializeNativeTarget()

    val error = BytePointer()
    val executionEngineRef = LLVMExecutionEngineRef()

    LLVM.LLVMCreateJITCompilerForModule(executionEngineRef, ref, level.value.toInt(), error)

    if (!error.isNull) {
      throw LLVMError(error.getString(Charsets.UTF_8))
    }

    return ExecutionEngine(executionEngineRef)
  }

  public actual fun getGlobalIFunc(name: String): GlobalIFunc? {
    return LLVM.LLVMGetNamedGlobalIFunc(ref, name, name.length.toLong())?.let(::GlobalIFunc)
  }

  public actual fun addGlobalIFunc(
    name: String,
    type: FunctionType,
    addrSpace: AddrSpace,
    resolver: Function?,
  ): GlobalIFunc {
    val ref = LLVM.LLVMAddGlobalIFunc(
      ref,
      name,
      name.length.toLong(),
      type.ref,
      addrSpace.value.toInt(),
      resolver?.ref
    )

    return GlobalIFunc(ref)
  }

  public actual fun getGlobalVariable(name: String): GlobalVariable? {
    return LLVM.LLVMGetNamedGlobal(ref, name)?.let(::GlobalVariable)
  }

  public actual fun addGlobalVariable(
    name: String,
    type: Type,
    addrSpace: AddrSpace,
  ): GlobalVariable {
    return GlobalVariable(LLVM.LLVMAddGlobal(ref, type.ref, name))
  }

  public actual fun getGlobalAlias(name: String): GlobalAlias? {
    return LLVM.LLVMGetNamedGlobalAlias(ref, name, name.length.toLong())?.let(::GlobalAlias)
  }

  public actual fun addGlobalAlias(
    name: String,
    type: PointerType,
    constant: Constant,
  ): GlobalAlias {
    return GlobalAlias(LLVM.LLVMAddAlias(ref, type.ref, constant.ref, name))
  }

  public actual fun getFunction(name: String): Function? {
    return LLVM.LLVMGetNamedFunction(ref, name)?.let(::Function)
  }

  public actual fun addFunction(name: String, type: FunctionType): Function {
    return Function(LLVM.LLVMAddFunction(ref, name, type.ref))
  }

  public actual fun verify() {
    val message = BytePointer()

    LLVM.LLVMVerifyModule(ref, LLVM.LLVMReturnStatusAction, message)

    if (!message.isNull) {
      throw LLVMError(message.getString(Charsets.UTF_8))
    }
  }

  public override fun close() {
    LLVM.LLVMDisposeModule(ref)
  }

  public actual override fun toString(): String {
    return LLVM.LLVMPrintModuleToString(ref)!!.getString(Charsets.UTF_8)
  }
}
