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

import org.plank.llvm4k.Context

public expect sealed class Type {
  public open val context: Context
  public open val isSized: Boolean
  public open val size: ConstantInt
  public open val align: ConstantInt
  public open val kind: Kind

  public open fun pointer(addrSpace: AddrSpace = AddrSpace.Generic): PointerType

  public open fun constPointerNull(): ConstantPointerNull

  public override fun toString(): String

  public enum class Kind {
    Void,
    Half,
    Float,
    Double,
    X86_FP80,
    FP128,
    PPC_FP128,
    Label,
    Integer,
    Function,
    Struct,
    Array,
    Pointer,
    Vector,
    Metadata,
    X86_MMX,
    Token,
    ScalableVector,
    BFloat,
    X86_AMX;

    public val value: UInt

    public companion object {
      public fun byValue(value: Int): Kind

      public fun byValue(value: UInt): Kind
    }
  }
}

public expect class StructType : Type {
  public val name: String?

  public val isPacked: Boolean
  public val isOpaque: Boolean
  public val isLiteral: Boolean
  public val hasName: Boolean

  public var elements: List<Type>

  public val constantNull: ConstantAggregate

  public fun getConstant(vararg elements: Constant, isPacked: Boolean = false): ConstantAggregate
}

public expect sealed class CompositeType : Type {
  public abstract val count: Int
  public val elements: List<Type>
  public val contained: Type
}

public expect sealed class VectorType : CompositeType {
  public val constantNull: ConstantDataVector
}

public expect class FixedVectorType(contained: Type, count: Int) : VectorType

public expect class ScalableVectorType(contained: Type, minCount: Int) : VectorType

public expect class ArrayType(contained: Type, count: Int) : CompositeType {
  public val constantNull: ConstantDataArray
}

public expect class PointerType(
  contained: Type,
  addrSpace: AddrSpace = AddrSpace.Generic,
) : CompositeType

public expect class IntegerType : Type {
  public val constantNull: ConstantInt
  public val allOnes: ConstantInt
  public val typeWidth: Int

  public fun getConstant(value: Int, unsigned: Boolean = false): ConstantInt

  public fun getConstant(value: Long, unsigned: Boolean = false): ConstantInt
}

public expect class FloatType : Type {
  public val constantNull: ConstantFP
  public val allOnes: ConstantFP

  public fun getConstant(value: Float): ConstantFP

  public fun getConstant(value: Double): ConstantFP
}

public expect class FunctionType(
  returnType: Type,
  params: List<Type>,
  isVarargs: Boolean = false,
) : Type {
  public val returnType: Type
  public val isVarargs: Boolean
  public val parameters: List<Type>
}

public fun FunctionType(
  returnType: Type,
  vararg params: Type,
  isVarargs: Boolean = false,
): FunctionType {
  return FunctionType(returnType, params.toList(), isVarargs)
}

public expect class VoidType : Type

public expect class LabelType : Type

public expect class MetadataType : Type

public expect class TokenType : Type

public expect class X86MMXType : Type
