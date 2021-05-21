package com.lorenzoog.plank.compiler.builder

import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.compiler.buildGEP
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.shared.Right
import com.lorenzoog.plank.shared.either
import org.llvm4j.llvm4j.Value

fun CompilerContext.getField(value: Value, index: Int): CodegenResult = either {
  val indices = listOf(
    runtime.types.int.getConstant(0),
    runtime.types.int.getConstant(index),
  )

  Right(buildGEP(value, indices, name = "struct.gep.tmp"))
}
