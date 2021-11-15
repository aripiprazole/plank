package com.gabrielleeg1.plank.compiler.builder

import arrow.core.Either
import arrow.core.computations.either
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildAlloca
import com.gabrielleeg1.plank.compiler.buildGEP
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.buildStore
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Value

fun CompilerContext.getInstance(
  struct: NamedStructType,
  vararg arguments: Value,
  isPointer: Boolean = false,
): Either<CodegenError, Value> = either.eager {
  val instance = buildAlloca(struct, "${struct.getName()}.instance")

  arguments.forEachIndexed { index, value ->
    val field = getField(instance, index).bind()

    buildStore(field, value)
  }

  if (isPointer) {
    instance
  } else {
    buildLoad(instance, "${struct.getName()}.value")
  }
}

fun CompilerContext.getField(value: Value, index: Int): CodegenResult = either.eager {
  val indices = listOf(
    runtime.types.int.getConstant(0),
    runtime.types.int.getConstant(index),
  )

  buildGEP(value, indices, name = "struct.gep.tmp")
}
