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

import llvm.LLVMContextRef
import org.plank.llvm4k.ir.BasicBlock
import org.plank.llvm4k.ir.FloatType
import org.plank.llvm4k.ir.IntegerType
import org.plank.llvm4k.ir.LabelType
import org.plank.llvm4k.ir.MetadataType
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.TokenType
import org.plank.llvm4k.ir.VoidType
import org.plank.llvm4k.ir.X86MMXType

public actual interface Context : Disposable, Owner<LLVMContextRef> {
  public actual val void: VoidType
  public actual val i1: IntegerType
  public actual val i8: IntegerType
  public actual val i16: IntegerType
  public actual val i32: IntegerType
  public actual val i64: IntegerType
  public actual val i128: IntegerType
  public actual val float: FloatType
  public actual val bfloat: FloatType
  public actual val half: FloatType
  public actual val double: FloatType
  public actual val x86fp80: FloatType
  public actual val fp128: FloatType
  public actual val ppcFP128: FloatType
  public actual val x86MMX: X86MMXType
  public actual val label: LabelType
  public actual val metadata: MetadataType
  public actual val token: TokenType

  public actual fun createBasicBlock(name: String): BasicBlock

  public actual fun createIRBuilder(): IRBuilder

  public actual fun createIntegerType(bits: Int): IntegerType

  public actual fun createNamedStruct(name: String, builder: StructType.() -> Unit): StructType

  public actual fun createModule(name: String): Module
}

public actual object GlobalContext : Context by ContextImpl(llvm.LLVMGetGlobalContext()) {
  override fun toString(): String = "GlobalContext"
}

public actual fun Context(): Context {
  return ContextImpl(llvm.LLVMContextCreate())
}

internal class ContextImpl(override val ref: LLVMContextRef?) : Context {
  override val void: VoidType get() = VoidType(llvm.LLVMVoidTypeInContext(ref))
  override val i1: IntegerType get() = IntegerType(llvm.LLVMInt1TypeInContext(ref))
  override val i8: IntegerType get() = IntegerType(llvm.LLVMInt8TypeInContext(ref))
  override val i16: IntegerType get() = IntegerType(llvm.LLVMInt16TypeInContext(ref))
  override val i32: IntegerType get() = IntegerType(llvm.LLVMInt32TypeInContext(ref))
  override val i64: IntegerType get() = IntegerType(llvm.LLVMInt64TypeInContext(ref))
  override val i128: IntegerType get() = IntegerType(llvm.LLVMInt128TypeInContext(ref))
  override val float: FloatType get() = FloatType(llvm.LLVMFloatTypeInContext(ref))
  override val bfloat: FloatType get() = FloatType(llvm.LLVMBFloatTypeInContext(ref))
  override val half: FloatType get() = FloatType(llvm.LLVMHalfTypeInContext(ref))
  override val double: FloatType get() = FloatType(llvm.LLVMDoubleTypeInContext(ref))
  override val x86fp80: FloatType get() = FloatType(llvm.LLVMX86FP80TypeInContext(ref))
  override val fp128: FloatType get() = FloatType(llvm.LLVMFP128TypeInContext(ref))
  override val ppcFP128: FloatType get() = FloatType(llvm.LLVMPPCFP128TypeInContext(ref))
  override val x86MMX: X86MMXType get() = X86MMXType(llvm.LLVMX86MMXTypeInContext(ref))
  override val label: LabelType get() = LabelType(llvm.LLVMLabelTypeInContext(ref))
  override val metadata: MetadataType get() = MetadataType(llvm.LLVMMetadataTypeInContext(ref))
  override val token: TokenType get() = TokenType(llvm.LLVMTokenTypeInContext(ref))

  override fun createBasicBlock(name: String): BasicBlock {
    return BasicBlock(llvm.LLVMCreateBasicBlockInContext(ref, name))
  }

  override fun createIRBuilder(): IRBuilder {
    return IRBuilderImpl(llvm.LLVMCreateBuilderInContext(ref))
  }

  override fun createIntegerType(bits: Int): IntegerType {
    return IntegerType(llvm.LLVMIntTypeInContext(ref, bits.toUInt()))
  }

  override fun createNamedStruct(name: String, builder: StructType.() -> Unit): StructType {
    return StructType(llvm.LLVMStructCreateNamed(ref, name)).apply(builder)
  }

  override fun createModule(name: String): Module {
    return Module(llvm.LLVMModuleCreateWithNameInContext(name, ref))
  }

  override fun close() {
    llvm.LLVMContextDispose(ref)
  }

  override fun toString(): String = "Context"
}

internal fun Context(ref: LLVMContextRef?): Context = ContextImpl(ref)
