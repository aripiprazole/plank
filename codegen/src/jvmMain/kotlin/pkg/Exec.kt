@file:JvmName("ExecJvm")

package org.plank.codegen.pkg

import java.io.File

private val runtime = Runtime.getRuntime()

actual val pathSeparator: String = File.pathSeparator

actual fun Command.exec(): String {
  val process = runtime.exec(toString())

  if (process.waitFor() != 0) {
    throw CommandFailedException(toString(), process.exitValue())
  }

  return process.inputStream.readBytes().decodeToString()
}
