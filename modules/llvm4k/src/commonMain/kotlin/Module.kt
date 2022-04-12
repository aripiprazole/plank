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

public expect class Module : Disposable {
  public val context: Context
  public var inlineAsm: String
  public var sourceFilename: String
  public var moduleIdentifier: String
  public var dataLayout: String

  public fun dump(file: String)

  public fun appendInlineAsm(asm: String)

  public fun writeBitcode(file: String)

  public fun createJITExecutionEngine(level: OptimizationLevel = OptimizationLevel.None): ExecutionEngine

  public fun getTypeByName(name: String): StructType?

  public fun getGlobalIFunc(name: String): GlobalIFunc?

  public fun addGlobalIFunc(
    name: String,
    type: FunctionType,
    addrSpace: AddrSpace = AddrSpace.Generic,
    resolver: Function?,
  ): GlobalIFunc

  public fun getGlobalVariable(name: String): GlobalVariable?

  public fun addGlobalVariable(
    name: String,
    type: Type,
    addrSpace: AddrSpace = AddrSpace.Generic,
  ): GlobalVariable

  public fun getGlobalAlias(name: String): GlobalAlias?

  public fun addGlobalAlias(name: String, type: PointerType, constant: Constant): GlobalAlias

  public fun getFunction(name: String): Function?

  public fun addFunction(name: String, type: FunctionType): Function

  public fun verify()

  public override fun toString(): String
}
