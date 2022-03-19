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

import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import llvm.LLVMBasicBlockRef
import llvm.LLVMValueRef
import org.plank.llvm4k.toInt

public actual sealed class GlobalObject : GlobalValue()

public actual class GlobalIFunc(public override val ref: LLVMValueRef?) : GlobalObject() {
  public actual val hasResolver: Boolean get() = resolver != null

  public actual var resolver: Function?
    get() = llvm.LLVMGetGlobalIFuncResolver(ref)?.let(::Function)
    set(value) {
      llvm.LLVMSetGlobalIFuncResolver(ref, value?.ref)
    }

  public actual fun erase() {
    llvm.LLVMEraseGlobalIFunc(ref)
  }

  public actual fun delete() {
    llvm.LLVMRemoveGlobalIFunc(ref)
  }
}

public actual class GlobalVariable(public override val ref: LLVMValueRef?) : GlobalObject() {
  public actual var threadLocal: Boolean
    get(): Boolean = llvm.LLVMIsThreadLocal(ref) == 1
    set(value) {
      llvm.LLVMSetThreadLocal(ref, value.toInt())
    }

  public actual var threadLocalMode: ThreadLocalMode
    get(): ThreadLocalMode = ThreadLocalMode.byValue(llvm.LLVMGetThreadLocalMode(ref))
    set(value) {
      llvm.LLVMSetThreadLocalMode(ref, value.llvm)
    }

  public actual var constant: Boolean
    get(): Boolean = llvm.LLVMIsGlobalConstant(ref) == 1
    set(value) {
      llvm.LLVMSetGlobalConstant(ref, value.toInt())
    }

  public actual var externallyInitialized: Boolean
    get(): Boolean = llvm.LLVMIsExternallyInitialized(ref) == 1
    set(value) {
      llvm.LLVMSetExternallyInitialized(ref, value.toInt())
    }

  public actual var initializer: Constant?
    get(): Constant? = llvm.LLVMGetInitializer(ref)?.let(::Value) as Constant?
    set(value) {
      llvm.LLVMSetInitializer(ref, value?.ref)
    }

  public actual fun delete() {
    llvm.LLVMDeleteGlobal(ref)
  }
}

public actual class Function(public override val ref: LLVMValueRef?) : GlobalObject() {
  public actual override val type: FunctionType
    get(): FunctionType = (super.type as PointerType).contained as FunctionType

  public actual val hasGC: Boolean get() = gc != null
  public actual val isVarargs: Boolean get() = type.isVarargs
  public actual val returnType: Type get() = type.returnType

  public actual val hasPersonalityFn: Boolean get() = llvm.LLVMHasPersonalityFn(ref) == 1

  public actual var personalityFn: Function?
    get(): Function? = when {
      hasPersonalityFn -> Function(llvm.LLVMGetPersonalityFn(ref))
      else -> null
    }
    set(value) {
      llvm.LLVMSetPersonalityFn(ref, value?.ref)
    }

  public actual var callingConv: CallingConv
    get(): CallingConv = CallingConv.byValue(llvm.LLVMGetFunctionCallConv(ref))
    set(value) {
      llvm.LLVMSetFunctionCallConv(ref, value.llvm.value)
    }

  public actual var gc: String?
    get(): String? = llvm.LLVMGetGC(ref)?.toKString()
    set(value) {
      llvm.LLVMSetGC(ref, value)
    }

  public actual val arguments: List<Argument>
    get(): List<Argument> = memScoped {
      val size = llvm.LLVMCountParams(ref).toInt()
      val arguments = allocArray<CPointerVarOf<LLVMValueRef>>(size)

      llvm.LLVMGetParams(ref, arguments)

      (0 until size).map { Argument(arguments[it]) }
    }

  public actual val blocks: List<BasicBlock>
    get(): List<BasicBlock> = memScoped {
      val size = llvm.LLVMCountParams(ref).toInt()
      val blocks = allocArray<CPointerVarOf<LLVMBasicBlockRef>>(size)

      llvm.LLVMGetBasicBlocks(ref, blocks)

      (0 until size).map { BasicBlock(blocks[it]) }
    }

  public actual val entry: BasicBlock get() = BasicBlock(llvm.LLVMGetEntryBasicBlock(ref))

  public actual fun appendBasicBlock(block: BasicBlock) {
    llvm.LLVMAppendExistingBasicBlock(ref, block.ref)
  }

  public actual fun verify(action: VerifierFailureAction): Int {
    return llvm.LLVMVerifyFunction(ref, action.llvm)
  }

  public actual fun verify(): Boolean {
    return llvm.LLVMVerifyFunction(ref, llvm.LLVMVerifierFailureAction.LLVMReturnStatusAction) == 0
  }

  public actual operator fun invoke(builder: Function.() -> Unit): Function {
    return apply(builder)
  }

  public actual fun delete() {
    llvm.LLVMDeleteFunction(ref)
  }
}
