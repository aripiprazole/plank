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

public expect sealed class GlobalObject : GlobalValue

public expect class GlobalIFunc : GlobalObject {
  public var resolver: Function?

  public val hasResolver: Boolean

  public fun erase()

  public fun delete()
}

public expect class GlobalVariable : GlobalObject {
  public var threadLocal: Boolean
  public var threadLocalMode: ThreadLocalMode
  public var constant: Boolean
  public var externallyInitialized: Boolean
  public var initializer: Constant?

  public fun delete()
}

public expect class Function : GlobalObject {
  public override val type: FunctionType

  public val hasGC: Boolean
  public val hasPersonalityFn: Boolean

  public var personalityFn: Function?
  public val arguments: List<Argument>
  public val isVarargs: Boolean
  public val returnType: Type
  public var callingConv: CallingConv
  public var gc: String?
  public val blocks: List<BasicBlock>
  public val entry: BasicBlock

  public fun verify(action: VerifierFailureAction): Int
  public fun verify(): Boolean

  public fun appendBasicBlock(block: BasicBlock)

  public fun delete()

  public operator fun invoke(builder: Function.() -> Unit = {}): Function
}
