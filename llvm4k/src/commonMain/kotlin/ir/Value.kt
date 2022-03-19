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

public expect sealed class Value {
  public open val type: Type
  public open val kind: Kind

  public open val isConstant: Boolean
  public open val isUndef: Boolean
  public open val asBasicBlock: BasicBlock

  public open fun replace(other: Value)

  public override fun toString(): String

  public enum class Kind {
    Argument,
    BasicBlock,
    MemoryUse,
    MemoryDef,
    MemoryPhi,
    Function,
    GlobalAlias,
    GlobalIFunc,
    GlobalVariable,
    BlockAddress,
    ConstantExpr,
    ConstantArray,
    ConstantStruct,
    ConstantVector,
    UndefValue,
    ConstantAggregateZero,
    ConstantDataArray,
    ConstantDataVector,
    ConstantInt,
    ConstantFP,
    ConstantPointerNull,
    ConstantTokenNone,
    MetadataAsValue,
    InlineAsm,
    Instruction,
    PoisonValue;

    public val value: UInt

    public companion object {
      public fun byValue(value: Int): Kind

      public fun byValue(value: UInt): Kind
    }
  }
}

public expect interface NamedValue {
  public open var name: String
}
