package com.lorenzoog.plank.compiler.builder

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildAlloca
import com.lorenzoog.plank.compiler.buildGEP
import com.lorenzoog.plank.compiler.buildLoad
import com.lorenzoog.plank.compiler.buildStore
import com.lorenzoog.plank.compiler.instructions.CodegenError
import com.lorenzoog.plank.shared.Either
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Value

fun CompilerContext.getInstance(
  struct: NamedStructType,
  vararg arguments: Value
): Either<CodegenError, Value> = either {
  val instance = buildAlloca(struct, "${struct.getName()}.instance")

  arguments.forEachIndexed { index, value ->
    val indices = listOf(
      runtime.types.int.getConstant(0),
      runtime.types.int.getConstant(index),
    )

    val field = buildGEP(instance, indices, name = "gep.tmp")

    buildStore(field, value)
  }

  Right(buildLoad(instance, "${struct.getName()}.value"))
}
