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

import org.plank.llvm4k.ir.BasicBlock
import org.plank.llvm4k.ir.FloatType
import org.plank.llvm4k.ir.IntegerType
import org.plank.llvm4k.ir.LabelType
import org.plank.llvm4k.ir.MetadataType
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.TokenType
import org.plank.llvm4k.ir.VoidType
import org.plank.llvm4k.ir.X86MMXType
import kotlin.js.JsName

public expect interface Context : Disposable {
  public val void: VoidType
  public val i1: IntegerType
  public val i8: IntegerType
  public val i16: IntegerType
  public val i32: IntegerType
  public val i64: IntegerType
  public val i128: IntegerType
  public val float: FloatType
  public val bfloat: FloatType
  public val half: FloatType
  public val double: FloatType
  public val x86fp80: FloatType
  public val fp128: FloatType
  public val ppcFP128: FloatType
  public val x86MMX: X86MMXType
  public val label: LabelType
  public val metadata: MetadataType
  public val token: TokenType

  public fun createBasicBlock(name: String): BasicBlock

  public fun createIRBuilder(): IRBuilder

  public fun createIntegerType(bits: Int): IntegerType

  public fun createNamedStruct(name: String, builder: StructType.() -> Unit = {}): StructType

  public fun createModule(name: String): Module
}

public expect object GlobalContext : Context

@JsName("Context0")
public expect fun Context(): Context
