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

import llvm.LLVMOpcode.*
import llvm.LLVMValueRef

public actual sealed class ConstantExpr : Constant()

public actual class BinaryConstantExpr(public override val ref: LLVMValueRef?) :
  ConstantExpr()

public actual class CompareConstantExpr(public override val ref: LLVMValueRef?) :
  ConstantExpr()

public actual class ExtractElementConstantExpr(public override val ref: LLVMValueRef?) :
  ConstantExpr()

public actual class ExtractValueConstantExpr(public override val ref: LLVMValueRef?) :
  ConstantExpr()

public actual class GetElementPtrConstantExpr(public override val ref: LLVMValueRef?) :
  ConstantExpr()

public actual class InsertElementConstantExpr(public override val ref: LLVMValueRef?) :
  ConstantExpr()

public actual class InsertValueConstantExpr(public override val ref: LLVMValueRef?) :
  ConstantExpr()

public actual class SelectConstantExpr(public override val ref: LLVMValueRef?) :
  ConstantExpr()

public actual class ShuffleVectorConstantExpr(public override val ref: LLVMValueRef?) :
  ConstantExpr()

public actual class UnaryConstantExpr(public override val ref: LLVMValueRef?) :
  ConstantExpr()

@Suppress("ComplexMethod", "LongMethod")
public fun ConstantExpr(ref: LLVMValueRef?): ConstantExpr {
  return when (val opcode = llvm.LLVMGetConstOpcode(ref)) {
    LLVMAdd -> BinaryConstantExpr(ref)
    LLVMFAdd -> BinaryConstantExpr(ref)
    LLVMSub -> BinaryConstantExpr(ref)
    LLVMFSub -> BinaryConstantExpr(ref)
    LLVMMul -> BinaryConstantExpr(ref)
    LLVMFMul -> BinaryConstantExpr(ref)
    LLVMUDiv -> BinaryConstantExpr(ref)
    LLVMSDiv -> BinaryConstantExpr(ref)
    LLVMFDiv -> BinaryConstantExpr(ref)
    LLVMURem -> BinaryConstantExpr(ref)
    LLVMSRem -> BinaryConstantExpr(ref)
    LLVMFRem -> BinaryConstantExpr(ref)
    LLVMShl -> BinaryConstantExpr(ref)
    LLVMLShr -> BinaryConstantExpr(ref)
    LLVMAShr -> BinaryConstantExpr(ref)
    LLVMAnd -> BinaryConstantExpr(ref)
    LLVMOr -> BinaryConstantExpr(ref)
    LLVMXor -> BinaryConstantExpr(ref)
    LLVMGetElementPtr -> GetElementPtrConstantExpr(ref)
    LLVMICmp -> CompareConstantExpr(ref)
    LLVMFCmp -> CompareConstantExpr(ref)
    LLVMExtractElement -> ExtractElementConstantExpr(ref)
    LLVMInsertElement -> InsertElementConstantExpr(ref)
    LLVMShuffleVector -> ShuffleVectorConstantExpr(ref)
    LLVMExtractValue -> ExtractValueConstantExpr(ref)
    LLVMInsertValue -> InsertValueConstantExpr(ref)
    LLVMFNeg -> UnaryConstantExpr(ref)
    else -> error("Unsupported constant expression $opcode")
  }
}
