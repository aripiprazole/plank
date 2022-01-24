package com.gabrielleeg1.plank.compiler.builder

import com.gabrielleeg1.plank.compiler.CompilerContext
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Value

fun CompilerContext.getInstance(
  struct: NamedStructType,
  vararg arguments: Value,
  isPointer: Boolean = false,
  name: String = "${struct.getName()}.instance",
): Value {
  val instance = buildAlloca(struct, name)

  arguments.forEachIndexed { index, value ->
    val field = getField(instance, index, name = "$name.GET.$index")

    buildStore(field, value)
  }

  return if (isPointer) {
    instance
  } else {
    buildLoad(instance, "$name.value")
  }
}

fun CompilerContext.getField(
  value: Value,
  index: Int,
  name: String = "struct.gep.tmp",
): Value {
  val indices = listOf(
    runtime.types.int.getConstant(0),
    runtime.types.int.getConstant(index),
  )

  return buildGEP(value, indices, name = name)
}
