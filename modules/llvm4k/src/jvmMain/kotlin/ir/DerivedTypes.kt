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

@file:JvmName("DerivedTypesJvm")

package org.plank.llvm4k.ir

import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.llvm.LLVM.LLVMTypeRef
import org.bytedeco.llvm.global.LLVM
import org.bytedeco.llvm.global.LLVM.LLVMArrayTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMBFloatTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMDoubleTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMFP128TypeKind
import org.bytedeco.llvm.global.LLVM.LLVMFloatTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMFunctionTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMGetTypeContext
import org.bytedeco.llvm.global.LLVM.LLVMHalfTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMIntegerTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMLabelTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMMetadataTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMPPC_FP128TypeKind
import org.bytedeco.llvm.global.LLVM.LLVMPointerTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMScalableVectorTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMStructTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMTokenTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMVectorTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMVoidTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMX86_AMXTypeKind
import org.bytedeco.llvm.global.LLVM.LLVMX86_FP80TypeKind
import org.bytedeco.llvm.global.LLVM.LLVMX86_MMXTypeKind
import org.plank.llvm4k.Context
import org.plank.llvm4k.Owner
import org.plank.llvm4k.printToString
import org.plank.llvm4k.toInt
import org.plank.llvm4k.toPointerPointer

public actual sealed class Type : Owner<LLVMTypeRef> {
  public actual open val context: Context get() = Context(LLVMGetTypeContext(ref))
  public actual open val isSized: Boolean get() = LLVM.LLVMTypeIsSized(ref) == 1
  public actual open val size: ConstantInt get() = ConstantInt(LLVM.LLVMSizeOf(ref))
  public actual open val align: ConstantInt get() = ConstantInt(LLVM.LLVMAlignOf(ref))
  public actual open val kind: Kind get() = Kind.byValue(LLVM.LLVMGetTypeKind(ref))

  public actual open fun pointer(addrSpace: AddrSpace): PointerType {
    return PointerType(this, addrSpace)
  }

  public actual open fun constPointerNull(): ConstantPointerNull {
    return ConstantPointerNull(LLVM.LLVMConstPointerNull(ref))
  }

  public actual override fun toString(): String = printToString()

  public actual enum class Kind(public val llvm: Int) {
    Void(LLVMVoidTypeKind),
    Half(LLVMHalfTypeKind),
    Float(LLVMFloatTypeKind),
    Double(LLVMDoubleTypeKind),
    X86_FP80(LLVMX86_FP80TypeKind),
    FP128(LLVMFP128TypeKind),
    PPC_FP128(LLVMPPC_FP128TypeKind),
    Label(LLVMLabelTypeKind),
    Integer(LLVMIntegerTypeKind),
    Function(LLVMFunctionTypeKind),
    Struct(LLVMStructTypeKind),
    Array(LLVMArrayTypeKind),
    Pointer(LLVMPointerTypeKind),
    Vector(LLVMVectorTypeKind),
    Metadata(LLVMMetadataTypeKind),
    X86_MMX(LLVMX86_MMXTypeKind),
    Token(LLVMTokenTypeKind),
    ScalableVector(LLVMScalableVectorTypeKind),
    BFloat(LLVMBFloatTypeKind),
    X86_AMX(LLVMX86_AMXTypeKind);

    public actual val value: UInt get() = llvm.toUInt()

    public actual companion object {
      public actual fun byValue(value: Int): Kind {
        return byValue(value.toUInt())
      }

      public actual fun byValue(value: UInt): Kind {
        return values().single { it.value == value }
      }
    }
  }
}

public actual class StructType(public override val ref: LLVMTypeRef?) : Type() {
  public actual val name: String? get() = LLVM.LLVMGetStructName(ref)?.getString(Charsets.UTF_8)

  public actual val isPacked: Boolean get() = LLVM.LLVMIsPackedStruct(ref) == 1
  public actual val isOpaque: Boolean get() = LLVM.LLVMIsOpaqueStruct(ref) == 1
  public actual val isLiteral: Boolean get() = LLVM.LLVMIsLiteralStruct(ref) == 1
  public actual val hasName: Boolean get() = name != null

  public actual var elements: List<Type>
    get(): List<Type> {
      require(!isOpaque) { "Cannot get elements of opaque struct" }

      val size = LLVM.LLVMCountStructElementTypes(ref)
      val arguments = PointerPointer<LLVMTypeRef>()

      LLVM.LLVMGetStructElementTypes(ref, arguments)

      return (0 until size).map { Type(LLVMTypeRef(arguments.get(it.toLong()))) }
    }
    set(value): Unit {
      LLVM.LLVMStructSetBody(
        ref,
        value.map { it.ref }.toPointerPointer(),
        value.size,
        isPacked.toInt(),
      )
    }

  public actual val constantNull: ConstantAggregate
    get(): ConstantAggregate = ConstantAggregate(LLVM.LLVMConstNull(ref))

  public actual fun getConstant(
    vararg elements: Constant,
    isPacked: Boolean,
  ): ConstantAggregate {
    val ref = when (name) {
      null -> LLVM.LLVMConstStructInContext(
        context.ref,
        elements.map { it.ref }.toPointerPointer(),
        elements.size,
        isPacked.toInt(),
      )
      else -> {
        LLVM.LLVMConstNamedStruct(
          ref,
          elements.map { it.ref }.toPointerPointer(),
          elements.size
        )
      }
    }

    return ConstantAggregate(ref)
  }
}

public actual sealed class CompositeType : Type() {
  public actual abstract val count: Int
  public actual val contained: Type get() = Type(LLVM.LLVMGetElementType(ref))

  public actual val elements: List<Type>
    get() {
      val arguments = PointerPointer<LLVMTypeRef>()

      LLVM.LLVMGetSubtypes(ref, arguments)

      return (0 until count).map { Type(LLVMTypeRef(arguments.get(it.toLong()))) }
    }
}

public actual sealed class VectorType : CompositeType() {
  public override val count: Int get() = LLVM.LLVMGetVectorSize(ref)

  public actual val constantNull: ConstantDataVector
    get() = ConstantDataVector(LLVM.LLVMConstNull(ref))
}

public actual class FixedVectorType(public override val ref: LLVMTypeRef?) : VectorType() {
  public actual constructor(contained: Type, count: Int) :
    this(LLVM.LLVMVectorType(contained.ref, count))
}

public actual class ScalableVectorType(public override val ref: LLVMTypeRef?) : VectorType() {
  public actual constructor(contained: Type, minCount: Int) :
    this(LLVM.LLVMScalableVectorType(contained.ref, minCount))
}

public actual class ArrayType(public override val ref: LLVMTypeRef?) : CompositeType() {
  public override val count: Int get() = LLVM.LLVMGetArrayLength(ref)

  public actual val constantNull: ConstantDataArray
    get() = ConstantDataArray(LLVM.LLVMConstNull(ref))

  public actual constructor(contained: Type, count: Int) :
    this(LLVM.LLVMArrayType(contained.ref, count))
}

public actual class PointerType(public override val ref: LLVMTypeRef?) : CompositeType() {
  public override val count: Int = 1

  public actual constructor(contained: Type, addrSpace: AddrSpace) :
    this(LLVM.LLVMPointerType(contained.ref, addrSpace.value.toInt()))
}

public actual class IntegerType(public override val ref: LLVMTypeRef?) : Type() {
  public actual val constantNull: ConstantInt get() = ConstantInt(LLVM.LLVMConstNull(ref))
  public actual val allOnes: ConstantInt get() = ConstantInt(LLVM.LLVMConstAllOnes(ref))
  public actual val typeWidth: Int get() = LLVM.LLVMGetIntTypeWidth(ref)

  public actual fun getConstant(value: Int, unsigned: Boolean): ConstantInt {
    return getConstant(value.toLong(), unsigned)
  }

  public actual fun getConstant(value: Long, unsigned: Boolean): ConstantInt {
    return ConstantInt(LLVM.LLVMConstInt(ref, value, unsigned.toInt()))
  }
}

public actual class FloatType(public override val ref: LLVMTypeRef?) : Type() {
  public actual val constantNull: ConstantFP get() = ConstantFP(LLVM.LLVMConstNull(ref))
  public actual val allOnes: ConstantFP get() = ConstantFP(LLVM.LLVMConstAllOnes(ref))

  public actual fun getConstant(value: Float): ConstantFP {
    return getConstant(value.toDouble())
  }

  public actual fun getConstant(value: Double): ConstantFP {
    return ConstantFP(LLVM.LLVMConstReal(ref, value))
  }
}

public actual class FunctionType(public override val ref: LLVMTypeRef?) : Type() {
  public actual constructor(returnType: Type, params: List<Type>, isVarargs: Boolean) :
    this(
      LLVM.LLVMFunctionType(
        returnType.ref,
        params.map { it.ref }.toPointerPointer(),
        params.size,
        isVarargs.toInt()
      )
    )

  public actual val returnType: Type get() = Type(LLVM.LLVMGetReturnType(ref))
  public actual val isVarargs: Boolean get() = LLVM.LLVMIsFunctionVarArg(ref) == 1

  public actual val parameters: List<Type>
    get(): List<Type> {
      val size = LLVM.LLVMCountParamTypes(ref)
      val arguments = PointerPointer<LLVMTypeRef>()

      LLVM.LLVMGetParamTypes(ref, arguments)

      return (0 until size).map { Type(LLVMTypeRef(arguments.get(it.toLong()))) }
    }
}

public actual class VoidType(public override val ref: LLVMTypeRef?) : Type()

public actual class LabelType(public override val ref: LLVMTypeRef?) : Type()

public actual class MetadataType(public override val ref: LLVMTypeRef?) : Type()

public actual class TokenType(public override val ref: LLVMTypeRef?) : Type()

public actual class X86MMXType(public override val ref: LLVMTypeRef?) : Type()

@Suppress("ComplexMethod")
public fun Type(ref: LLVMTypeRef?): Type {
  return when (val kind = LLVM.LLVMGetTypeKind(ref)) {
    LLVMVoidTypeKind -> VoidType(ref)
    LLVMHalfTypeKind -> FloatType(ref)
    LLVMFloatTypeKind -> FloatType(ref)
    LLVMDoubleTypeKind -> FloatType(ref)
    LLVMX86_FP80TypeKind -> FloatType(ref)
    LLVMFP128TypeKind -> FloatType(ref)
    LLVMPPC_FP128TypeKind -> FloatType(ref)
    LLVMLabelTypeKind -> LabelType(ref)
    LLVMIntegerTypeKind -> IntegerType(ref)
    LLVMFunctionTypeKind -> FunctionType(ref)
    LLVMStructTypeKind -> StructType(ref)
    LLVMArrayTypeKind -> ArrayType(ref)
    LLVMPointerTypeKind -> PointerType(ref)
    LLVMVectorTypeKind -> FixedVectorType(ref)
    LLVMMetadataTypeKind -> MetadataType(ref)
    LLVMX86_MMXTypeKind -> X86MMXType(ref)
    LLVMTokenTypeKind -> TokenType(ref)
    LLVMScalableVectorTypeKind -> ScalableVectorType(ref)
    LLVMBFloatTypeKind -> FloatType(ref)
    LLVMX86_AMXTypeKind -> FloatType(ref)
    else -> error("Unknown type kind: $kind")
  }
}
