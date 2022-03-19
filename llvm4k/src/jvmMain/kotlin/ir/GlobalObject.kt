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

import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef
import org.bytedeco.llvm.LLVM.LLVMValueRef
import org.bytedeco.llvm.global.LLVM
import org.plank.llvm4k.toInt

public actual sealed class GlobalObject : GlobalValue()

public actual class GlobalIFunc(public override val ref: LLVMValueRef?) : GlobalObject() {
  public actual val hasResolver: Boolean get() = resolver != null

  public actual var resolver: Function?
    get() = LLVM.LLVMGetGlobalIFuncResolver(ref)?.let(::Function)
    set(value) {
      LLVM.LLVMSetGlobalIFuncResolver(ref, value?.ref)
    }

  public actual fun erase() {
    LLVM.LLVMEraseGlobalIFunc(ref)
  }

  public actual fun delete() {
    LLVM.LLVMRemoveGlobalIFunc(ref)
  }
}

public actual class GlobalVariable(public override val ref: LLVMValueRef?) : GlobalObject() {
  public actual var threadLocal: Boolean
    get(): Boolean = LLVM.LLVMIsThreadLocal(ref) == 1
    set(value) {
      LLVM.LLVMSetThreadLocal(ref, value.toInt())
    }

  public actual var threadLocalMode: ThreadLocalMode
    get(): ThreadLocalMode = ThreadLocalMode.byValue(LLVM.LLVMGetThreadLocalMode(ref))
    set(value) {
      LLVM.LLVMSetThreadLocalMode(ref, value.llvm)
    }

  public actual var constant: Boolean
    get(): Boolean = LLVM.LLVMIsGlobalConstant(ref) == 1
    set(value) {
      LLVM.LLVMSetGlobalConstant(ref, value.toInt())
    }

  public actual var externallyInitialized: Boolean
    get(): Boolean = LLVM.LLVMIsExternallyInitialized(ref) == 1
    set(value) {
      LLVM.LLVMSetExternallyInitialized(ref, value.toInt())
    }

  public actual var initializer: Constant?
    get(): Constant? = LLVM.LLVMGetInitializer(ref)?.let(::Value) as Constant?
    set(value) {
      LLVM.LLVMSetInitializer(ref, value?.ref)
    }

  public actual fun delete() {
    LLVM.LLVMDeleteGlobal(ref)
  }
}

public actual class Function(public override val ref: LLVMValueRef?) : GlobalObject() {
  public actual override val type: FunctionType
    get(): FunctionType = (super.type as PointerType).contained as FunctionType

  public actual val hasGC: Boolean get() = gc != null
  public actual val isVarargs: Boolean get() = type.isVarargs
  public actual val returnType: Type get() = type.returnType

  public actual val hasPersonalityFn: Boolean get() = LLVM.LLVMHasPersonalityFn(ref) == 1

  public actual var personalityFn: Function?
    get(): Function? = when {
      hasPersonalityFn -> Function(LLVM.LLVMGetPersonalityFn(ref))
      else -> null
    }
    set(value) {
      LLVM.LLVMSetPersonalityFn(ref, value?.ref)
    }

  public actual var callingConv: CallingConv
    get(): CallingConv = CallingConv.byValue(LLVM.LLVMGetFunctionCallConv(ref))
    set(value) {
      LLVM.LLVMSetFunctionCallConv(ref, value.llvm)
    }

  public actual var gc: String?
    get(): String? = LLVM.LLVMGetGC(ref)?.getString(Charsets.UTF_8)
    set(value) {
      LLVM.LLVMSetGC(ref, value)
    }

  public actual val arguments: List<Argument>
    get(): List<Argument> {
      val size = LLVM.LLVMCountParams(ref)
      val arguments = PointerPointer<LLVMValueRef>(size.toLong())

      LLVM.LLVMGetParams(ref, arguments)

      return (0 until size).map { Argument(LLVMValueRef(arguments.get(it.toLong()))) }
    }

  public actual val blocks: List<BasicBlock>
    get(): List<BasicBlock> {
      val size = LLVM.LLVMCountParams(ref)
      val blocks = PointerPointer<LLVMBasicBlockRef>(size.toLong())

      LLVM.LLVMGetBasicBlocks(ref, blocks)

      return (0 until size).map { BasicBlock(LLVMBasicBlockRef(blocks.get(it.toLong()))) }
    }

  public actual val entry: BasicBlock get() = BasicBlock(LLVM.LLVMGetEntryBasicBlock(ref))

  public actual fun appendBasicBlock(block: BasicBlock) {
    LLVM.LLVMAppendExistingBasicBlock(ref, block.ref)
  }

  public actual fun verify(action: VerifierFailureAction): Int {
    return LLVM.LLVMVerifyFunction(ref, action.llvm)
  }

  public actual fun verify(): Boolean {
    return LLVM.LLVMVerifyFunction(ref, LLVM.LLVMReturnStatusAction) == 0
  }

  public actual operator fun invoke(builder: Function.() -> Unit): Function {
    return apply(builder)
  }

  public actual fun delete() {
    LLVM.LLVMDeleteFunction(ref)
  }
}
