@file:JvmName("ExecJvm")

package org.plank.codegen.pkg

import java.io.File

private val runtime = Runtime.getRuntime()

val pathSeparator: String = File.pathSeparator

fun Command.exec(): String {
  val process = runtime.exec(toString())

  if (process.waitFor() != 0) {
    throw CommandFailedException(
      toString(),
      process.exitValue(),
      process.inputStream.readBytes().decodeToString(),
    )
  }

  return process.inputStream.readBytes().decodeToString()
}
