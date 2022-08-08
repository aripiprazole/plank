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

public expect sealed class ConstantData : Constant

public expect class ConstantAggregate : ConstantData

public expect sealed class ConstantDataSequential : ConstantData

public expect class ConstantDataArray : ConstantDataSequential

public expect class ConstantDataVector : ConstantDataSequential

public expect class ConstantFP : ConstantData {
  public val realValue: FPValue
  public val value: Float
}

public data class FPValue(public val value: Float, public val lossy: Boolean)

public expect class ConstantInt : ConstantData {
  public val zExtValue: Long
  public val sExtValue: Long
}

public expect class ConstantPointerNull : ConstantData

public expect class ConstantTokenNone : ConstantData

public expect sealed class UndefValue : ConstantData

public expect class PoisonValue : UndefValue
