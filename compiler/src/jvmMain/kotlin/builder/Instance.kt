package com.gabrielleeg1.plank.compiler.builder

import com.gabrielleeg1.plank.compiler.CompilerContext
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Value

inline fun CompilerContext.getInstance(
  struct: NamedStructType,
  vararg arguments: Value,
  isPointer: Boolean = false,
  name: String = "v.${struct.getName()}",
  generateGEPName: (Int, String) -> String = { index, value -> "$value.[$index]" }
): Value {
  val instance = buildAlloca(struct, name)

  arguments.forEachIndexed { index, value ->
    val field = getField(instance, index, name = generateGEPName(index, name))

    buildStore(field, value)
  }

  return if (isPointer) {
    instance
  } else {
    buildLoad(instance, "load.$name")
  }
}

fun CompilerContext.getField(
  value: Value,
  index: Int,
  name: String? = null,
): Value {
  val indices = listOf(
    runtime.types.int.getConstant(0),
    runtime.types.int.getConstant(index),
  )

  return buildGEP(value, indices, name = name)
}
