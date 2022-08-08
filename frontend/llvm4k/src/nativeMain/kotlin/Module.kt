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

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import llvm.LLVMExecutionEngineRefVar
import llvm.LLVMModuleRef
import llvm.LLVMVerifierFailureAction.LLVMReturnStatusAction
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
import platform.posix.size_tVar

public actual class Module(public override val ref: LLVMModuleRef?) :
  Disposable,
  Owner<LLVMModuleRef> {
  public actual val context: Context get() = Context(llvm.LLVMGetModuleContext(ref))

  public actual var inlineAsm: String
    get(): String = memScoped {
      val size = alloc<size_tVar>()

      llvm.LLVMGetModuleInlineAsm(ref, size.ptr)!!.toKString()
    }
    set(value) {
      llvm.LLVMSetModuleInlineAsm2(ref, value, value.length.toULong())
    }

  public actual var sourceFilename: String
    get(): String = memScoped {
      val size = alloc<size_tVar>()

      llvm.LLVMGetSourceFileName(ref, size.ptr)!!.toKString()
    }
    set(value) {
      llvm.LLVMSetSourceFileName(ref, value, value.length.toULong())
    }

  public actual var moduleIdentifier: String
    get(): String = memScoped {
      val size = alloc<size_tVar>()

      llvm.LLVMGetModuleIdentifier(ref, size.ptr)!!.toKString()
    }
    set(value) {
      llvm.LLVMSetModuleIdentifier(ref, value, value.length.toULong())
    }

  public actual var dataLayout: String
    get(): String = llvm.LLVMGetDataLayout(ref)!!.toKString()
    set(value) {
      llvm.LLVMSetDataLayout(ref, value)
    }

  public actual fun dump(file: String): Unit = memScoped {
    val message = alloc<CPointerVar<ByteVar>>()

    llvm.LLVMPrintModuleToFile(ref, file, message.ptr)

    if (message.value != null) {
      throw LLVMError(message.value!!.toKString())
    }
  }

  public actual fun appendInlineAsm(asm: String) {
    llvm.LLVMAppendModuleInlineAsm(ref, asm, asm.length.toULong())
  }

  public actual fun writeBitcode(file: String) {
    llvm.LLVMWriteBitcodeToFile(ref, file)
  }

  public actual fun getTypeByName(name: String): StructType? {
    return llvm.LLVMGetTypeByName(ref, name)?.let(::StructType)
  }

  public actual fun createJITExecutionEngine(level: OptimizationLevel): ExecutionEngine {
    llvm.LLVMInitializeNativeTarget()

    return memScoped {
      val error = alloc<CPointerVar<ByteVar>>()
      val executionEngineRef = alloc<LLVMExecutionEngineRefVar>()

      llvm.LLVMCreateJITCompilerForModule(executionEngineRef.ptr, ref, level.value, error.ptr)

      if (error.value != null) {
        throw LLVMError(error.value!!.toKString())
      }

      ExecutionEngine(executionEngineRef.value)
    }
  }

  public actual fun getGlobalIFunc(name: String): GlobalIFunc? {
    return llvm.LLVMGetNamedGlobalIFunc(ref, name, name.length.toULong())?.let(::GlobalIFunc)
  }

  public actual fun addGlobalIFunc(
    name: String,
    type: FunctionType,
    addrSpace: AddrSpace,
    resolver: Function?,
  ): GlobalIFunc {
    val ref = llvm.LLVMAddGlobalIFunc(
      ref,
      name,
      name.length.toULong(),
      type.ref,
      addrSpace.value,
      resolver?.ref,
    )

    return GlobalIFunc(ref)
  }

  public actual fun getGlobalVariable(name: String): GlobalVariable? {
    return llvm.LLVMGetNamedGlobal(ref, name)?.let(::GlobalVariable)
  }

  public actual fun addGlobalVariable(
    name: String,
    type: Type,
    addrSpace: AddrSpace,
  ): GlobalVariable {
    return GlobalVariable(llvm.LLVMAddGlobal(ref, type.ref, name))
  }

  public actual fun getGlobalAlias(name: String): GlobalAlias? {
    return llvm.LLVMGetNamedGlobalAlias(ref, name, name.length.toULong())?.let(::GlobalAlias)
  }

  public actual fun addGlobalAlias(
    name: String,
    type: PointerType,
    constant: Constant,
  ): GlobalAlias {
    return GlobalAlias(llvm.LLVMAddAlias(ref, type.ref, constant.ref, name))
  }

  public actual fun getFunction(name: String): Function? {
    return llvm.LLVMGetNamedFunction(ref, name)?.let(::Function)
  }

  public actual fun addFunction(name: String, type: FunctionType): Function {
    return Function(llvm.LLVMAddFunction(ref, name, type.ref))
  }

  public actual fun verify(): Unit = memScoped {
    val message = alloc<CPointerVar<ByteVar>>()

    llvm.LLVMVerifyModule(ref, LLVMReturnStatusAction, message.ptr)

    if (message.value != null) {
      throw LLVMError(message.value!!.toKString())
    }
  }

  public override fun close() {
    llvm.LLVMDisposeModule(ref)
  }

  public actual override fun toString(): String {
    return llvm.LLVMPrintModuleToString(ref)!!.toKString()
  }
}
